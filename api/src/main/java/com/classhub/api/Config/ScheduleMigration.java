package com.classhub.api.Config;

import com.classhub.api.domain.ApiDisciplina;
import com.classhub.api.domain.ApiHorario;
import com.classhub.api.domain.ApiProfessor;
import com.classhub.api.domain.ApiTurma;
import com.classhub.api.domain.ApiVinculo;
import com.classhub.api.repository.ApiDisciplinaRepository;
import com.classhub.api.repository.ApiHorarioRepository;
import com.classhub.api.repository.ApiTurmaRepository;
import com.classhub.api.repository.ApiVinculoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.Normalizer;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Atualiza as grades já persistidas, inclusive quando a carga de dados demo está desativada. */
@Configuration
public class ScheduleMigration {
    private static final Logger LOG = Logger.getLogger(ScheduleMigration.class.getName());
    private static final List<String> COURSES = List.of("port", "mat", "cien", "hist", "geo", "prog", "ing", "edfis", "arte");
    private static final int[][] WEEK = {
            {0, 1, 2, 3, 6, 4},
            {1, 0, 5, 7, 2, 4},
            {0, 1, 3, 5, 8, 6},
            {1, 2, 0, 4, 5, 7},
            {0, 1, 3, 8, 6, 7}
    };
    private static final LocalTime[][] PERIODS = {
            {LocalTime.of(13, 15), LocalTime.of(14, 5)},
            {LocalTime.of(14, 5), LocalTime.of(14, 55)},
            {LocalTime.of(14, 55), LocalTime.of(15, 45)},
            {LocalTime.of(16, 0), LocalTime.of(16, 50)},
            {LocalTime.of(16, 50), LocalTime.of(17, 40)},
            {LocalTime.of(17, 40), LocalTime.of(18, 30)}
    };
    private static final Map<String, Integer> LOADS = Map.of(
            "port", 5, "mat", 5, "cien", 3, "hist", 3, "geo", 3,
            "prog", 3, "ing", 3, "edfis", 3, "arte", 2);

    @Bean
    @Order(2)
    CommandLineRunner migratePersistedSchedules(ApiTurmaRepository turmas, ApiDisciplinaRepository disciplinas,
                                                ApiVinculoRepository vinculos, ApiHorarioRepository horarios,
                                                PlatformTransactionManager transactionManager) {
        return args -> new TransactionTemplate(transactionManager).executeWithoutResult(status ->
                synchronize(turmas, disciplinas, vinculos, horarios));
    }

    private void synchronize(ApiTurmaRepository turmaRepository, ApiDisciplinaRepository disciplinaRepository,
                             ApiVinculoRepository vinculoRepository, ApiHorarioRepository horarioRepository) {
        List<ApiTurma> allTurmas = turmaRepository.findAll().stream()
                .sorted(Comparator.comparing(ApiTurma::getNome, String.CASE_INSENSITIVE_ORDER)).toList();
        if (allTurmas.isEmpty()) return;

        List<ApiVinculo> allLinks = vinculoRepository.findAll();
        List<ApiHorario> allSchedules = horarioRepository.findAll();
        Map<Long, List<ApiHorario>> schedulesByClass = allSchedules.stream()
                .collect(Collectors.groupingBy(h -> h.getTurma().getId()));
        Map<String, CourseTeacher> globalTeachers = new HashMap<>();
        allLinks.forEach(link -> addCourse(globalTeachers, link.getDisciplina(), link.getProfessor()));
        allSchedules.forEach(schedule -> addCourse(globalTeachers, schedule.getDisciplina(), schedule.getProfessor()));

        List<ClassPlan> plans = new ArrayList<>();
        for (ApiTurma turma : allTurmas) {
            Map<String, CourseTeacher> courses = new HashMap<>();
            allLinks.stream().filter(v -> v.getTurma().getId().equals(turma.getId()))
                    .forEach(v -> addCourse(courses, v.getDisciplina(), v.getProfessor()));
            schedulesByClass.getOrDefault(turma.getId(), List.of()).forEach(h ->
                    addCourse(courses, h.getDisciplina(), h.getProfessor()));
            for (String key : COURSES) courses.putIfAbsent(key, globalTeachers.get(key));
            if (COURSES.stream().allMatch(key -> courses.get(key) != null)) plans.add(new ClassPlan(turma, courses));
        }
        if (plans.isEmpty()) return;

        Set<Long> plannedClassIds = plans.stream().map(p -> p.turma().getId()).collect(Collectors.toSet());
        if (isCurrentScheduleCorrect(plans, schedulesByClass, allSchedules, plannedClassIds)) return;

        // Reserve existing periods for classes that lack enough subject/teacher data to be migrated safely.
        Set<String> occupied = new HashSet<>();
        for (ApiHorario schedule : allSchedules) {
            if (plannedClassIds.contains(schedule.getTurma().getId())) continue;
            for (int slot = 0; slot < PERIODS.length; slot++) {
                if (overlaps(schedule.getHoraInicio(), schedule.getHoraFim(), PERIODS[slot][0], PERIODS[slot][1])) {
                    occupied.add(occupation(schedule.getDiaSemana(), slot, schedule.getProfessor().getId()));
                }
            }
        }

        List<List<String[]>> classOrdersByDay = new ArrayList<>();
        for (int day = 0; day < 5; day++) classOrdersByDay.add(new ArrayList<>());
        for (int day = 0; day < 5; day++) {
            for (ClassPlan plan : plans) {
                String[] ordered = arrangeDay(WEEK[day], plan, day + 1, occupied);
                if (ordered == null) {
                    LOG.warning("Grade não atualizada: não foi possível evitar choque de professor na turma " + plan.turma().getNome());
                    return;
                }
                classOrdersByDay.get(day).add(ordered);
            }
        }

        for (ClassPlan plan : plans) {
            plan.turma().setTurno("VESPERTINO");
            turmaRepository.save(plan.turma());
            horarioRepository.deleteAll(schedulesByClass.getOrDefault(plan.turma().getId(), List.of()));
        }
        horarioRepository.flush();
        for (int day = 0; day < 5; day++) {
            for (int classIndex = 0; classIndex < plans.size(); classIndex++) {
                ClassPlan plan = plans.get(classIndex);
                String[] order = classOrdersByDay.get(day).get(classIndex);
                for (int slot = 0; slot < 6; slot++) {
                    CourseTeacher course = plan.courses().get(order[slot]);
                    ApiHorario schedule = new ApiHorario();
                    schedule.setTurma(plan.turma());
                    schedule.setDisciplina(course.disciplina());
                    schedule.setProfessor(course.professor());
                    schedule.setDiaSemana(day + 1);
                    schedule.setHoraInicio(PERIODS[slot][0]);
                    schedule.setHoraFim(PERIODS[slot][1]);
                    horarioRepository.save(schedule);
                }
            }
        }
        LOG.info("Grades semanais atualizadas para " + plans.size() + " turma(s).");
    }

    private String[] arrangeDay(int[] courseIndexes, ClassPlan plan, int day, Set<String> occupied) {
        String[] result = new String[6];
        List<String> remaining = Arrays.stream(courseIndexes).mapToObj(COURSES::get).collect(Collectors.toCollection(ArrayList::new));
        if (arrangeSlot(remaining, result, 0, plan, day, occupied)) return result;
        return null;
    }

    private boolean arrangeSlot(List<String> remaining, String[] result, int slot, ClassPlan plan, int day, Set<String> occupied) {
        if (slot == result.length) return true;
        Set<String> tried = new HashSet<>();
        for (int index = 0; index < remaining.size(); index++) {
            String key = remaining.get(index);
            if (!tried.add(key)) continue;
            String occupancy = occupation(day, slot, plan.courses().get(key).professor().getId());
            if (occupied.contains(occupancy)) continue;
            remaining.remove(index);
            result[slot] = key;
            occupied.add(occupancy);
            if (arrangeSlot(remaining, result, slot + 1, plan, day, occupied)) return true;
            occupied.remove(occupancy);
            remaining.add(index, key);
        }
        return false;
    }

    private boolean isCurrentScheduleCorrect(List<ClassPlan> plans, Map<Long, List<ApiHorario>> byClass,
                                             List<ApiHorario> all, Set<Long> plannedIds) {
        Map<String, Integer> expectedLoads = LOADS;
        for (ClassPlan plan : plans) {
            List<ApiHorario> current = byClass.getOrDefault(plan.turma().getId(), List.of());
            if (current.size() != 30) return false;
            if (current.stream().anyMatch(h -> courseKey(h.getDisciplina().getNome()) == null)) return false;
            Map<String, Long> counts = current.stream().collect(Collectors.groupingBy(
                    h -> courseKey(h.getDisciplina().getNome()), Collectors.counting()));
            if (expectedLoads.entrySet().stream().anyMatch(e -> counts.getOrDefault(e.getKey(), 0L).longValue() != e.getValue().longValue())) return false;
            if (current.stream().anyMatch(h -> !validPeriod(h) || courseKey(h.getDisciplina().getNome()) == null
                    || !expectedLoads.containsKey(courseKey(h.getDisciplina().getNome())))) return false;
            for (int day = 1; day <= 5; day++) {
                final int currentDay = day;
                if (current.stream().filter(h -> h.getDiaSemana() == currentDay).count() != 6) return false;
            }
        }
        List<ApiHorario> managed = all.stream().filter(h -> plannedIds.contains(h.getTurma().getId())).toList();
        for (int i = 0; i < managed.size(); i++) {
            ApiHorario a = managed.get(i);
            for (int j = i + 1; j < managed.size(); j++) {
                ApiHorario b = managed.get(j);
                if (a.getTurma().getId().equals(b.getTurma().getId())) continue;
                if (a.getDiaSemana().equals(b.getDiaSemana()) && a.getProfessor().getId().equals(b.getProfessor().getId())
                        && overlaps(a.getHoraInicio(), a.getHoraFim(), b.getHoraInicio(), b.getHoraFim())) return false;
            }
        }
        return true;
    }

    private boolean validPeriod(ApiHorario h) {
        return h.getDiaSemana() != null && h.getDiaSemana() >= 1 && h.getDiaSemana() <= 5
                && Arrays.stream(PERIODS).anyMatch(p -> p[0].equals(h.getHoraInicio()) && p[1].equals(h.getHoraFim()));
    }

    private void addCourse(Map<String, CourseTeacher> map, ApiDisciplina discipline, ApiProfessor professor) {
        String key = courseKey(discipline.getNome());
        if (key != null) map.putIfAbsent(key, new CourseTeacher(discipline, professor));
    }

    private String courseKey(String name) {
        if (name == null) return null;
        String value = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
        if (value.contains("portugues")) return "port";
        if (value.contains("matemat")) return "mat";
        if (value.contains("cienc")) return "cien";
        if (value.contains("hist")) return "hist";
        if (value.contains("geograf")) return "geo";
        if (value.contains("program")) return "prog";
        if (value.contains("ingles")) return "ing";
        if (value.contains("educacao fisica") || value.contains("ed fisica")) return "edfis";
        if (value.contains("arte")) return "arte";
        return null;
    }

    private boolean overlaps(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private String occupation(int day, int slot, Long professorId) {
        return day + ":" + slot + ":" + professorId;
    }

    private record CourseTeacher(ApiDisciplina disciplina, ApiProfessor professor) { }
    private record ClassPlan(ApiTurma turma, Map<String, CourseTeacher> courses) { }
}