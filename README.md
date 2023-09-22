# Virtual Threads Performance Example

This project is a simple Spring Boot web application running Java 21 and a pre-release version of Spring Boot 3.2 that supports [virtual threads](https://openjdk.org/jeps/444).

## Running

Enable or disable the virtual threads support within [application.properties](src/main/resources/application.properties)

### Web Application
- Install Java 21
- In a terminal, start the Spring Boot application

```shell
./gradlew bootRun
```
- This will start a Spring Boot API with one test endpoint at `http://localhost:8080/test`

### Load Test

- Install [k6](https://k6.io/)
- In the command line run 
```shell
k6 run script.js
```
- This runs the load test with the configuration defined in [script.js](script.js).

## Results
The expected request duration for this endpoint is 1 second. When using k6, you'll see many metrics displayed after your execution is complete. For a full list of metrics, check the [k6 documentation](https://k6.io/docs/using-k6/metrics/reference/). 

### Platform Threads
The following is the output of running 1,000 users against the endpoint with a gradual ramp-up and ramp-down phase.

Notice the `http_req_duration` value averaging out to be 3.47s per request. This is due to the Platform threads block execution and do not allow further requests to be processed by the server.

```text
❯ k6 run script.js

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: script.js
     output: -

  scenarios: (100.00%) 1 scenario, 1000 max VUs, 1m10s max duration (incl. graceful stop):
           * contacts: Up to 1000 looping VUs for 40s over 2 stages (gracefulRampDown: 10s, gracefulStop: 30s)


     data_received..................: 612 kB 14 kB/s
     data_sent......................: 704 kB 16 kB/s
     http_req_blocked...............: avg=54.96µs min=1µs med=5µs   max=761µs p(90)=299µs p(95)=432µs
     http_req_connecting............: avg=38.63µs min=0s  med=0s    max=617µs p(90)=232µs p(95)=338µs
     http_req_duration..............: avg=3.47s   min=1s  med=4.01s max=4.02s p(90)=4.01s p(95)=4.01s
       { expected_response:true }...: avg=3.47s   min=1s  med=4.01s max=4.02s p(90)=4.01s p(95)=4.01s
     http_req_failed................: 0.00%  ✓ 0          ✗ 8380  
     http_req_receiving.............: avg=41.47µs min=8µs med=37µs  max=213µs p(90)=65µs  p(95)=76µs 
     http_req_sending...............: avg=26.64µs min=5µs med=20µs  max=235µs p(90)=52µs  p(95)=70µs 
     http_req_tls_handshaking.......: avg=0s      min=0s  med=0s    max=0s    p(90)=0s    p(95)=0s   
     http_req_waiting...............: avg=3.47s   min=1s  med=4.01s max=4.02s p(90)=4.01s p(95)=4.01s
     http_reqs......................: 8380   186.176936/s
     iteration_duration.............: avg=4.47s   min=2s  med=5.01s max=5.02s p(90)=5.01s p(95)=5.01s
     iterations.....................: 8380   186.176936/s
     vus............................: 19     min=19       max=1000
     vus_max........................: 1000   min=1000     max=1000


running (0m45.0s), 0000/1000 VUs, 8380 complete and 0 interrupted iterations
contacts ✓ [======================================] 0000/1000 VUs  40s

```

### Virtual Threads
The following is the output of running 1,000 users against the endpoint with a gradual ramp-up and ramp-down phase.

Notice the `http_req_duration` value averaging out to be 1s per request. This is because of the usage of virtual threads, which allow the application to unblock the main platform threads and continue to receive traffic from consumers.

```text
❯ k6 run script.js

          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: script.js
     output: -

  scenarios: (100.00%) 1 scenario, 1000 max VUs, 1m10s max duration (incl. graceful stop):
           * contacts: Up to 1000 looping VUs for 40s over 2 stages (gracefulRampDown: 10s, gracefulStop: 30s)


     data_received..................: 2.2 MB 51 kB/s
     data_sent......................: 1.5 MB 36 kB/s
     http_req_blocked...............: avg=27.53µs min=1µs  med=5µs  max=2.44ms p(90)=10µs p(95)=268µs
     http_req_connecting............: avg=17.5µs  min=0s   med=0s   max=1.48ms p(90)=0s   p(95)=212µs
     http_req_duration..............: avg=1s      min=1s   med=1s   max=1.1s   p(90)=1s   p(95)=1s   
       { expected_response:true }...: avg=1s      min=1s   med=1s   max=1.1s   p(90)=1s   p(95)=1s   
     http_req_failed................: 0.00%  ✓ 0          ✗ 17980 
     http_req_receiving.............: avg=49.77µs min=12µs med=45µs max=1.23ms p(90)=77µs p(95)=92µs 
     http_req_sending...............: avg=21.89µs min=5µs  med=17µs max=1.11ms p(90)=37µs p(95)=53µs 
     http_req_tls_handshaking.......: avg=0s      min=0s   med=0s   max=0s     p(90)=0s   p(95)=0s   
     http_req_waiting...............: avg=1s      min=1s   med=1s   max=1.1s   p(90)=1s   p(95)=1s   
     http_reqs......................: 17980  428.055796/s
     iteration_duration.............: avg=2s      min=2s   med=2s   max=2.1s   p(90)=2s   p(95)=2s   
     iterations.....................: 17980  428.055796/s
     vus............................: 32     min=32       max=1000
     vus_max........................: 1000   min=1000     max=1000

running (0m42.0s), 0000/1000 VUs, 17980 complete and 0 interrupted iterations
contacts ✓ [======================================] 0000/1000 VUs  40s
```

## Summary
In our simplistic example, Platform threads executed 8,380 requests with an average duration of 3.47s. Virtual threads outperformed Platform threads by executing 17,980 requests with an average duration of 1s.

# Slides

![Slides](docs/slides.png)