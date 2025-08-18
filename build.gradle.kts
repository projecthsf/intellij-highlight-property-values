// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

plugins {
  id("java")
  id("org.jetbrains.intellij.platform") version "2.6.0"
}

group = "io.github.projecthsf.property.highlight"
version = "1.1.1"

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    intellijIdeaCommunity("2024.2.6")
    //phpstorm("2024.2.6")
    //goland("2024.1.1")
    //pycharmCommunity("2024.2.6")
    //datagrip("2025.1.3")
    //bundledPlugin("com.intellij.database")
    //bundledPlugin("com.intellij.java")
    //bundledPlugin("org.jetbrains.plugins.go")
  }
}

intellijPlatform {
  buildSearchableOptions = false

  pluginConfiguration {
    ideaVersion {
      sinceBuild = "241"
    }
  }
  pluginVerification  {
    ides {
      recommended()
    }
  }
}
