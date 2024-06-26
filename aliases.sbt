import SbtUtil._

addCommandAlias("l", "projects")
addCommandAlias("ll", "projects")
addCommandAlias("ls", "projects")
addCommandAlias("cd", "project")
addCommandAlias("root", "cd playground")
addCommandAlias("gen", "Antlr4 / antlr4Generate")
addCommandAlias("c", "compile")
addCommandAlias("ca", "Test / compile")
addCommandAlias("t", "test")
addCommandAlias("r", "run")
addCommandAlias("rs", "reStart")
addCommandAlias("s", "reStop")
addCommandAlias(
  "styleCheck",
  "scalafmtSbtCheck; scalafmtCheckAll; Test / compile; scalafixAll --check"
)
addCommandAlias(
  "styleFix",
  "Test / compile; scalafixAll; scalafmtSbt; scalafmtAll"
)
addCommandAlias(
  "up2date",
  "reload plugins; dependencyUpdates; reload return; dependencyUpdates"
)

onLoadMessage +=
  s"""|
      |╭──────────────────────────────────╮
      |│     List of defined ${styled("aliases")}      │
      |├─────────────┬────────────────────┤
      |│ ${styled("l")} | ${styled("ll")} | ${styled(
       "ls"
     )} │ projects           │
      |│ ${styled("cd")}          │ project            │
      |│ ${styled("root")}        │ cd root            │
      |│ ${styled("gen")}         │ regen parser/lexer │
      |│ ${styled("c")}           │ compile            │
      |│ ${styled("ca")}          │ compile all        │
      |│ ${styled("t")}           │ test               │
      |│ ${styled("r")}           │ run                │
      |│ ${styled("rs")}          │ reStart            │
      |│ ${styled("s")}           │ reStop             │
      |│ ${styled("styleCheck")}  │ fmt & fix check    │
      |│ ${styled("styleFix")}    │ fix then fmt       │
      |│ ${styled("up2date")}     │ dependencyUpdates  │
      |╰─────────────┴────────────────────╯""".stripMargin
