import { cn, getInitials } from '../../utils';

interface AvatarProps {
  src?: string;
  name: string;
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

const sizes = { sm: 'h-8 w-8 text-xs', md: 'h-10 w-10 text-sm', lg: 'h-14 w-14 text-lg' };

export function Avatar({ src, name, size = 'md', className }: AvatarProps) {
  return (
    <div className={cn('relative shrink-0 overflow-hidden rounded-full bg-primary-100 dark:bg-primary-900/30', sizes[size], className)}>
      {src ? (
        <img src={src} alt={name} className="h-full w-full object-cover" />
      ) : (
        <div className="flex h-full w-full items-center justify-center font-semibold text-primary-700 dark:text-primary-300">
          {getInitials(name)}
        </div>
      )}
    </div>
  );
}
