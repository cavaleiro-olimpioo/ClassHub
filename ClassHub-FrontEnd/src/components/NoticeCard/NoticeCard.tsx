import { Badge } from '../Badge/Badge';
import { formatDate } from '../../utils';
import type { Announcement } from '../../types';
import { Pin, Edit, Trash2 } from 'lucide-react';
import { Button } from '../Button/Button';

const categoryColors: Record<string, string> = {
  Geral: 'gray', Acadêmico: 'blue', Evento: 'purple', Urgente: 'red', Comunicado: 'green',
};

const priorityColors: Record<string, string> = {
  Baixa: 'gray', Normal: 'blue', Alta: 'red',
};

interface NoticeCardProps {
  announcement: Announcement;
  canEdit?: boolean;
  onEdit?: () => void;
  onDelete?: () => void;
}

export function NoticeCard({ announcement, canEdit, onEdit, onDelete }: NoticeCardProps) {
  return (
    <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition-shadow hover:shadow-md dark:border-gray-700 dark:bg-gray-800">
      <div className="flex items-start justify-between gap-3">
        <div className="flex-1">
          <div className="flex flex-wrap items-center gap-2">
            {announcement.priority === 'Alta' && <Pin className="h-4 w-4 text-accent-500" />}
            <h3 className="font-semibold text-gray-900 dark:text-gray-100">{announcement.title}</h3>
          </div>
          <p className="mt-2 text-sm text-gray-600 dark:text-gray-400 line-clamp-3">{announcement.description}</p>
          <div className="mt-3 flex flex-wrap items-center gap-2">
            <Badge color={categoryColors[announcement.category]}>{announcement.category}</Badge>
            <Badge color={priorityColors[announcement.priority]}>{announcement.priority}</Badge>
            <Badge color={announcement.status === 'Ativo' ? 'green' : 'gray'}>{announcement.status}</Badge>
          </div>
          <p className="mt-3 text-xs text-gray-400">
            {formatDate(announcement.date)} · {announcement.author}
          </p>
        </div>
        {canEdit && (
          <div className="flex gap-1">
            <Button variant="ghost" size="sm" onClick={onEdit}><Edit className="h-4 w-4" /></Button>
            <Button variant="ghost" size="sm" onClick={onDelete}><Trash2 className="h-4 w-4 text-accent-500" /></Button>
          </div>
        )}
      </div>
    </div>
  );
}
