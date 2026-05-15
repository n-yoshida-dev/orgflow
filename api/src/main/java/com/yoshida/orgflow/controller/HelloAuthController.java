package com.yoshida.orgflow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello-auth")
public class HelloAuthController {
  @GetMapping
  public String sayHello() {
    return "Hello auth!";
  }

}
