import { Badge } from '../Badge/Badge';
import { formatDate } from '../../utils';
import type { LostFoundItem } from '../../types';
import { Button } from '../Button/Button';
import { Hand } from 'lucide-react';

const statusColors: Record<string, string> = {
  Encontrado: 'blue', Reivindicado: 'yellow', Devolvido: 'green',
};

interface LostFoundCardProps {
  item: LostFoundItem;
  onClaim?: () => void;
}

export function LostFoundCard({ item, onClaim }: LostFoundCardProps) {
  return (
    <div className="rounded-xl border border-gray-200 bg-white overflow-hidden shadow-sm transition-shadow hover:shadow-md dark:border-gray-700 dark:bg-gray-800">
      {item.photo && (
        <div className="h-40 bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
          <img src={item.photo} alt={item.object} className="h-full w-full object-cover" />
        </div>
      )}
      <div className="p-4">
        <h3 className="font-semibold text-gray-900 dark:text-gray-100">{item.object}</h3>
        <p className="mt-1 text-sm text-gray-500">{item.description}</p>
        <div className="mt-3 flex flex-wrap gap-2">
          <Badge color="purple">{item.category}</Badge>
          <Badge color={statusColors[item.status]}>{item.status}</Badge>
        </div>
        <p className="mt-2 text-xs text-gray-400">{item.location} · {formatDate(item.date)}</p>
        {item.status === 'Encontrado' && onClaim && (
          <Button variant="outline" size="sm" className="mt-3 w-full" onClick={onClaim}>
            <Hand className="h-4 w-4" /> Encontrei este objeto
          </Button>
        )}
      </div>
    </div>
  );
}
