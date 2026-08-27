import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type {
  Student, Teacher, SchoolClass, Grade, AttendanceRecord,
  Occurrence, Announcement, CalendarEvent, LostFoundItem, Notification,
} from '../types';
import {
  STUDENTS, TEACHERS, CLASSES, GRADES, ATTENDANCE, OCCURRENCES,
  ANNOUNCEMENTS, CALENDAR_EVENTS, LOST_FOUND, NOTIFICATIONS, SUBJECTS, ROOMS, GUARDIANS, SCHEDULES,
} from '../data/mockData';

interface DataContextType {
  students: Student[];
  teachers: Teacher[];
  classes: SchoolClass[];
  grades: Grade[];
  attendance: AttendanceRecord[];
  occurrences: Occurrence[];
  announcements: Announcement[];
  events: CalendarEvent[];
  lostFound: LostFoundItem[];
  notifications: Notification[];
  subjects: typeof SUBJECTS;
  rooms: typeof ROOMS;
  guardians: typeof GUARDIANS;
  schedules: typeof SCHEDULES;
  setStudents: React.Dispatch<React.SetStateAction<Student[]>>;
  setTeachers: React.Dispatch<React.SetStateAction<Teacher[]>>;
  setClasses: React.Dispatch<React.SetStateAction<SchoolClass[]>>;
  setGrades: React.Dispatch<React.SetStateAction<Grade[]>>;
  setAttendance: React.Dispatch<React.SetStateAction<AttendanceRecord[]>>;
  setOccurrences: React.Dispatch<React.SetStateAction<Occurrence[]>>;
  setAnnouncements: React.Dispatch<React.SetStateAction<Announcement[]>>;
  setEvents: React.Dispatch<React.SetStateAction<CalendarEvent[]>>;
  setLostFound: React.Dispatch<React.SetStateAction<LostFoundItem[]>>;
  setNotifications: React.Dispatch<React.SetStateAction<Notification[]>>;
}

const DataContext = createContext<DataContextType | null>(null);

function loadFromStorage<T>(key: string, fallback: T): T {
  try {
    const saved = localStorage.getItem(key);
    return saved ? JSON.parse(saved) : fallback;
  } catch {
    return fallback;
  }
}

export function DataProvider({ children }: { children: ReactNode }) {
  const [students, setStudents] = useState<Student[]>(() => loadFromStorage('classhub_students', STUDENTS));
  const [teachers, setTeachers] = useState<Teacher[]>(() => loadFromStorage('classhub_teachers', TEACHERS));
  const [classes, setClasses] = useState<SchoolClass[]>(() => loadFromStorage('classhub_classes', CLASSES));
  const [grades, setGrades] = useState<Grade[]>(() => loadFromStorage('classhub_grades', GRADES));
  const [attendance, setAttendance] = useState<AttendanceRecord[]>(() => loadFromStorage('classhub_attendance', ATTENDANCE));
  const [occurrences, setOccurrences] = useState<Occurrence[]>(() => loadFromStorage('classhub_occurrences', OCCURRENCES));
  const [announcements, setAnnouncements] = useState<Announcement[]>(() => loadFromStorage('classhub_announcements', ANNOUNCEMENTS));
  const [events, setEvents] = useState<CalendarEvent[]>(() => loadFromStorage('classhub_events', CALENDAR_EVENTS));
  const [lostFound, setLostFound] = useState<LostFoundItem[]>(() => loadFromStorage('classhub_lostfound', LOST_FOUND));
  const [notifications, setNotifications] = useState<Notification[]>(() => loadFromStorage('classhub_notifications', NOTIFICATIONS));

  useEffect(() => { localStorage.setItem('classhub_students', JSON.stringify(students)); }, [students]);
  useEffect(() => { localStorage.setItem('classhub_teachers', JSON.stringify(teachers)); }, [teachers]);
  useEffect(() => { localStorage.setItem('classhub_classes', JSON.stringify(classes)); }, [classes]);
  useEffect(() => { localStorage.setItem('classhub_grades', JSON.stringify(grades)); }, [grades]);
  useEffect(() => { localStorage.setItem('classhub_attendance', JSON.stringify(attendance)); }, [attendance]);
  useEffect(() => { localStorage.setItem('classhub_occurrences', JSON.stringify(occurrences)); }, [occurrences]);
  useEffect(() => { localStorage.setItem('classhub_announcements', JSON.stringify(announcements)); }, [announcements]);
  useEffect(() => { localStorage.setItem('classhub_events', JSON.stringify(events)); }, [events]);
  useEffect(() => { localStorage.setItem('classhub_lostfound', JSON.stringify(lostFound)); }, [lostFound]);
  useEffect(() => { localStorage.setItem('classhub_notifications', JSON.stringify(notifications)); }, [notifications]);

  return (
    <DataContext.Provider value={{
      students, teachers, classes, grades, attendance, occurrences,
      announcements, events, lostFound, notifications,
      subjects: SUBJECTS, rooms: ROOMS, guardians: GUARDIANS, schedules: SCHEDULES,
      setStudents, setTeachers, setClasses, setGrades, setAttendance,
      setOccurrences, setAnnouncements, setEvents, setLostFound, setNotifications,
    }}>
      {children}
    </DataContext.Provider>
  );
}

export function useData() {
  const ctx = useContext(DataContext);
  if (!ctx) throw new Error('useData must be used within DataProvider');
  return ctx;
}
