Bash Profile Settings
=====================

#### .bash_profile file content

    HISTFILESIZE=20000
    homebrew=/usr/local/bin:/usr/local/sbin
    export ANT_HOME=/usr/local/apache-ant
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_45.jdk/Contents/Home
    export GRADLE_HOME=/usr/local/opt/gradle
    export PATH=$homebrew:$PATH:$JAVA_HOME/bin:/Users/*yourpath*/android/sdk/tools:/Users/*yourpath*/android/sdk/platform-tools:$GRADLE_HOME/bin:$ANT_HOME/bin