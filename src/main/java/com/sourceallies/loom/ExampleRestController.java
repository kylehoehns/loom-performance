package com.sourceallies.loom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleRestController {

  @GetMapping("/test")
  String execute() throws InterruptedException {
    Thread.sleep(1_000);
    return "success";
  }

}
