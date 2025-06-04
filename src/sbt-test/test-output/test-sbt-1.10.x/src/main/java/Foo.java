class Foo {
  // Should trigger COMMAND_INJECTION
  void commandInjection(String input) throws java.io.IOException {
    Runtime r = Runtime.getRuntime();
    r.exec("/bin/sh -c some_tool" + input);
  }
}
