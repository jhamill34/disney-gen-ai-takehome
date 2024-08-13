import { Navigation } from '@/components/nav';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Toaster } from '@/components/ui/toaster';
import { useToast } from '@/components/ui/use-toast';
import { Label } from '@radix-ui/react-label';
import { LoaderCircleIcon } from 'lucide-react';
import { useState } from 'react';
import {
  StompSessionProvider,
  useStompClient,
  useSubscription,
} from 'react-stomp-hooks';

interface IngestResponse {
  message: string;
  successful: boolean;
}

const IngestionForm: React.FC = () => {
  const [url, setUrl] = useState('');
  const sockClient = useStompClient();
  const [submitting, setSubmitting] = useState(false);
  const { toast } = useToast();

  useSubscription('/topic/ingeststatus', (m) => {
    setSubmitting(false);
    const response = JSON.parse(m.body) as IngestResponse;

    if (response.successful) {
      setUrl('');
      toast({
        title: 'Done',
        description: response.message,
      });
    } else {
      toast({
        title: 'Error',
        description: response.message,
      });
    }
  });

  return (
    <div className="max-w-5xl mx-auto">
      <Card className="m-8">
        <CardHeader>
          <CardTitle>
            <div className="flex gap-2 items-center">
              <h1>URL Ingestion</h1>
            </div>
          </CardTitle>
          <CardDescription>
            Provide a valid url to a web page that contains HTML to add it to
            the knowledge base
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form
            className="flex flex-col gap-2"
            onSubmit={(e) => {
              e.preventDefault();
              if (sockClient) {
                setSubmitting(true);
                sockClient.publish({
                  destination: '/app/ingest',
                  body: JSON.stringify({ url: url }),
                });
              }
            }}
          >
            <Label>Website URL</Label>
            <Input
              type="url"
              disabled={submitting}
              placeholder="http://example.com"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
            />
            <div className="flex">
              <Button type="submit" disabled={submitting}>
                {submitting ? (
                  <LoaderCircleIcon className="w-5 h-5 animate-spin" />
                ) : (
                  'Submit'
                )}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};

export const IngestionPage: React.FC = () => {
  return (
    <StompSessionProvider url="http://localhost:8080/ws">
      <Navigation />
      <IngestionForm />
      <Toaster />
    </StompSessionProvider>
  );
};
