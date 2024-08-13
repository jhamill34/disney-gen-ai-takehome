# Disney GenAI Take home

This project ingests different web pages and stores them in a vector database (I chose to use the PgVector extension of PostgreSQL). The ingestion and RAG pipeline are executed using the langchain4j library. The backend leverages the Spring Boot framework and the frontend uses ReactJS. 

## Getting started

### Running the database

Since the core of this is a postgres database with an extention we'll need to spin one of those up really quick using docker. The [Dockerfile](./docker/db/Dockerfile) can be built locally to create an image that will contain the required extention. 

```bash
# From the root directory
cd ./docker/db
docker build . -t pgvector:latest
```

Once that image is built and accessible locally, you can start up the [docker compose file](./deployments/docker-compose.yaml). 

```bash
# From the root
docker compose -f deployments/docker-compose.yaml up -d
```

This will start up a postgres container with port 5432 accessible. The default user/password/database are all called `disney-th`.  


### Running the Backend

The main services lives in the [web](./java/web) module and can be ran using the spring-boot-maven-plugin. This will start up a webserver on port 8080.

```bash
# Make sure to set your OpenAI key as the environment variable. 
export OPENAI_API_KEY=....
mvn spring-boot:run -pl :web
```

### Running the frontend 

The frontend is located in the [disney-th-ui](./javascript/disney-th-ui) forlder. Its just a ReactJS application that uses RSBuild as its build system. The styles are managed using tailwindcss with the help of some components from shadcn/ui. 

To start up this application using Bun run the following:

```bash
cd ./javascript/disney-th-ui
bun i
bun run dev
```

This will start up another webserver on port 3000. It has two pages `/app/chat` and `/app/ingest`. The chat page will ask for a user name and connect to the backend using websockets via the STOMP protocol. Messages will be sent to the backend and then the RAG pipeline on the backend will talk to OpenAI with the help of some context from our VectorDB to get an answer. When the answer is ready, its sent back to the client over websockets. 

The ingestion page is a simple form that sends a request (explained below) to ingest a web page via a url. 

### Ingesting data sets (i.e. websites)

On the `/app/ingest` page of the frontend, you can find a form that accepts a url to ingest a webpage. Its all done via a websocket connection to make the action (just like the chat interface) to make it async. 

The backend has a handler that accepts a url which will ingest the content of the page by parsing the HTML, splitting the text, then creating embeddings via OpenAI, and storing them into PgVector. 

Once the operation has completed, it responds with a message on the websocket indicating its completed so the frontend can display something to show the user. 

