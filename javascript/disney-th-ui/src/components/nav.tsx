import { NavLink } from 'react-router-dom';
import { cn } from '@/lib/utils';

export const Navigation: React.FC = () => {
  return (
    <nav>
      <ul className="flex gap-2 justify-center">
        <li>
          <NavLink
            to="/chat"
            className={({ isActive }) =>
              cn(
                'inline-block font-bold p-2 text-primary underline-offset-4 hover:underline',
                isActive && 'underline',
              )
            }
          >
            Chat
          </NavLink>
        </li>
        <li>
          <NavLink
            to="/ingest"
            className={({ isActive }) =>
              cn(
                'inline-block font-bold p-2 text-primary underline-offset-4 hover:underline',
                isActive && 'underline',
              )
            }
          >
            Ingest
          </NavLink>
        </li>
      </ul>
    </nav>
  );
};
