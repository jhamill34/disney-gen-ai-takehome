import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { cn } from '@/lib/utils';
import { DialogDescription, DialogTitle } from '@radix-ui/react-dialog';
import { CornerDownLeft, User } from 'lucide-react';
import { useState } from 'react';
import Markdown from 'react-markdown';
import {
  StompSessionProvider,
  useStompClient,
  useSubscription,
} from 'react-stomp-hooks';
import { v4 } from 'uuid';

interface User {
  userId: string;
  userName: string;
}

interface Sources {
  text: string;
  metadata: {
    index: number;
    url: string;
  };
}

interface ChatMessageResponse {
  data: ChatMessageEntry;
  sources: Sources[];
}

interface ChatMessageEntry {
  senderId: string;
  message: string;
}

interface ChatMessageEntryWithLinks extends ChatMessageEntry {
  links: string[];
}

interface ChatMessageProps {
  user: User;
  entry: ChatMessageEntryWithLinks;
}

const ChatMessageBubble: React.FC<ChatMessageProps> = ({
  user,
  entry,
}: ChatMessageProps) => {
  return (
    <div
      className={cn(
        'flex flex-col p-4 rounded-lg shadow-sm',
        user.userId === entry.senderId
          ? 'bg-primary text-primary-foreground ml-16'
          : 'bg-secondary text-secondary-foreground mr-16',
      )}
    >
      <div className="text-sm font-semibold">
        {user.userId === entry.senderId ? user.userName : entry.senderId}
      </div>
      <div className="flex-1 text-sm my-2">
        <Markdown>{entry.message}</Markdown>
      </div>
      {entry.links.length > 0 && (
        <div className="border-t pt-2">
          <span className="text-sm font-semibold text-muted-foreground">
            Sources
          </span>
          <ul className="flex flex-col">
            {entry.links.map((l) => (
              <li key={l} className="underline text-xs text-muted-foreground">
                <a href={l}>{l}</a>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

interface SetUserDialogProps {
  onSetUser: (name: User) => void;
}

const SetUserDialog: React.FC<SetUserDialogProps> = ({
  onSetUser,
}: SetUserDialogProps) => {
  const [userName, setUserName] = useState('');

  return (
    <Dialog defaultOpen>
      <DialogTrigger asChild>
        <Button variant="ghost">
          <User className="w-4 h-4" />
        </Button>
      </DialogTrigger>
      <DialogContent>
        <form
          className="flex flex-col gap-2"
          onSubmit={(e) => {
            e.preventDefault();
            if (userName !== '') {
              onSetUser({ userId: v4(), userName });
              setUserName('');
            }
          }}
        >
          <DialogTitle className="font-bold text-lg">Who are you?</DialogTitle>
          <DialogDescription className="text-sm text-muted-foreground italic">
            Set your user name to begin chatting with the AI
          </DialogDescription>
          <Label>User Name</Label>
          <Input
            type="text"
            placeholder="simba"
            value={userName}
            onChange={(e) => setUserName(e.target.value)}
          />
          <DialogFooter>
            <Button type="submit" disabled={userName === ''}>
              Submit
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
};

interface ChatInputProps {
  disabled: boolean;
  user: User | null;
  onSend: (message: ChatMessageEntry) => void;
}

export const ChatInput: React.FC<ChatInputProps> = ({
  disabled,
  user,
  onSend,
}: ChatInputProps) => {
  const [query, setQuery] = useState('');

  return (
    <form
      className="relative overflow-hidden rounded-lg border bg-background focus-within:ring-1 focus-within:ring-ring"
      onSubmit={(e) => {
        e.preventDefault();

        if (user !== null) {
          onSend({ senderId: user?.userId, message: query });
          setQuery('');
        }
      }}
    >
      <Label htmlFor="message" className="sr-only">
        Message
      </Label>
      <Textarea
        id="message"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        disabled={disabled}
        placeholder="Type your message here..."
        className="min-h-12 resize-none border-0 p-3 shadow-none focus-visible:ring-0"
      />
      <div className="flex items-center p-3 pt-0">
        <Button
          type="submit"
          size="sm"
          className="ml-auto gap-1.5"
          disabled={disabled}
        >
          Send Message
          <CornerDownLeft className="size-3.5" />
        </Button>
      </div>
    </form>
  );
};

const ChatPane: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [messages, setMessages] = useState<ChatMessageEntryWithLinks[]>([]);
  const sockClient = useStompClient();

  useSubscription('/topic/responses', (message) => {
    const response = JSON.parse(message.body) as ChatMessageResponse;
    const links = new Set<string>();
    for (const s of response.sources) {
      links.add(s.metadata.url);
    }

    setMessages((all) => [...all, { ...response.data, links: [...links] }]);
  });

  return (
    <div className="p-4 max-w-5xl m-auto">
      <div className="flex flex-row items-center border-b">
        <div className="flex-1">
          <h1 className="font-bold text-xl">Chat with DisneyAI</h1>
        </div>
        {user === null ? (
          <SetUserDialog onSetUser={setUser} />
        ) : (
          <span className="text-sm italic text-muted-foreground">
            Hi {user.userName}!
          </span>
        )}
      </div>
      {user !== null && (
        <div className="flex flex-col gap-4 p-4">
          {messages.map((m, i) => (
            <ChatMessageBubble key={i} user={user} entry={m} />
          ))}
        </div>
      )}
      <div>
        <ChatInput
          user={user}
          disabled={user === null}
          onSend={(m) => {
            if (sockClient) {
              sockClient.publish({
                destination: '/app/post',
                body: JSON.stringify(m),
              });
              setMessages((all) => [...all, { ...m, links: [] }]);
            }
          }}
        />
      </div>
    </div>
  );
};

export const ChatPage: React.FC = () => {
  return (
    <StompSessionProvider url="http://localhost:8080/ws">
      <ChatPane />
    </StompSessionProvider>
  );
};
