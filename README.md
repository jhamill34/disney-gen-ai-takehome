# Disney GenAI Take home

This project ingests different web pages and stores them in a vector database (I chose to use the PgVector extension of PostgreSQL). The ingestion and RAG pipeline are executed using the langchain4j library. The backend leverages the Spring Boot framework and the frontend uses ReactJS. 

## Getting started

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

The backend has an endpoint that accepts a url which will ingest the content of the page by parsing the HTML, splitting the text, then creating embeddings via OpenAI, and storing them into PgVector. 

This request is synchronous at the moment so it will take a while, but in theory could be converted to an async model using websockets (or something similar). 

To ingest a website run the following curl command from the cli:

```bash
curl -X POST \
    -H "Content-type: application/json" \
    -d '{ "url": "..." }' \
    http://localhost:8080/api/ingest
```
