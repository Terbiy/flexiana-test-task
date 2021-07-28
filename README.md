# flexiana-test-task

Flexiana test task consists of three steps. Every step has a dedicated branch, so it is possible to look at each solution in isolation.
 * Task 1 → `main` branch,
 * Task 2 → `task-2` branch,
 * Task 3 → `task-3` branch.

I didn't clear the boilerplate created by lein templates. I wanted to save some time and concentrate on the main. However, I do the cleanup for my Clojure pet project.

## Task 1

My solution and tests are in the following files:
 * `src/flexiana_test_task/core.clj`,
 * `test/flexiana_test_task/core_test.clj`.

The only command available for running here is `lein test`.

## Task 2

Switch to the branch `task-2`: `git checkout task-2`.

Here I've split my solution into three different files:
 * `src/flexiana_test_task/core.clj` as an entry point,
 * `src/flexiana_test_task/scramble.clj` to keep the scrambling functionality,
 * `src/flexiana_test_task/server.clj` for server.

I've chosen the Pedestal library for creating the API. I used it a few months ago and did some investigation choosing it among several others I could find. I don't remember the whole rationale, but I think some trust for the former me was sufficient to make this decision now.🙂

Use the `lein run` command to start the server.

Use the following commands to get different results once server is running:
 * Positive results: `curl "http://localhost:8890/scramble?source-string=asdasd&substring-candidate=asd"`,
 * Negative results: `curl "http://localhost:8890/scramble?source-string=asdasd&substring-candidate=asdf"`,
 * Missing source string error: `curl "http://localhost:8890/scramble?substring-candidate=asdf"`,
 * Missing substring candidate error: `curl "http://localhost:8890/scramble?source-string=asdasd"`,
 * Wrong source string value error: `curl "http://localhost:8890/scramble?source-string=asdasd1&substring-candidate=asdf"`,
 * Wrong substring candidate value error: `curl "http://localhost:8890/scramble?source-string=asdasd&substring-candidate=asd1"`.

It would also be great to have the automatic API tests, but I don't have experience in this area, and it could take some unpredictable amount of time from me.

## Task 3

Switch to the branch `task-3`: `git checkout task-3`.

For this task, I've introduced what we can call a monorepo. I've chosen the `reagent` technology for UI as I'm familiar with `react` and its component-oriented approach. For this task, I could possibly use a much easier setup and directly manipulate the DOM. However, I find the declarative `react` (or some other modern library or framework) approach so much more suitable for describing UIs, that I decided to stick to it.

There is a lot of boilerplate code I didn't touch, and the main changes happened here:
 * `client/src/cljs/client/core.cljs` the UI solution is from line 16 to line 93.
 * `client/resources/public/css/site.css` contains some styles updates.
 * `server/src/flexiana_test_task/server.clj` line 74 contains the permissive CORS update for development server configuration.

Running API server:
1. `cd server`
2. `lein repl`
3. `(use 'flexiana-test-task.server)`
4. `(start-dev)`

Running web server:
1. `cd client`
2. `npm install`
3. `npm install -g shadow-cljs` if not installed before.
4. `shadow-cljs watch app`
5. Navigate to http://localhost:3000/.

Here you can see two inputs. The UI doesn't handle any exceptional cases due to not allowing to type anything besides letters from `a` to `z`. I could possibly think of disabled or not loaded JavaScript or connection issues as the place where we need additional UI reflection. However, this might be a very long road to go. Also, connection issues do not make UI fail; I've tried.

I didn't add any tests here, but these are the ones I could think of:
 * Shallow rendering tests allowing to verify components non-failure and proper state as a reaction to certain actions.
 * E2E tests. I used Cypress for this purpose.

## License

Copyright © 2021 German Tebiev

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
