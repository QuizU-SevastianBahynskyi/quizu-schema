# QuizU Schema

## Purpose

The purpose of repository is to provide a schema for the QuizU application.  
The schema is created based on the database and distributed as a package among QuizU clients.

## How does it work?

The schema is created automatically based on the OpenApi spec which is provided by the QuizU API.  
As soon as new changes are made to the API spec, the OpenApi docs are updated in this repository.  
Then the workflow is triggered to publish package with the new schema to different package registries.

At the moment the following package registries are supported:
- Maven Central
  - Kotlin and Java are bundled in the same package
- NPM

> _NOTE:_ The pipeline is triggered only when the changes are made to the `main` branch from the `quizu-backend` pipeline.  
> (The email of commit in this case should be `github-actions[bot]@users.noreply.github.com`)
