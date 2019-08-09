(defproject com.github.gerritjvv/codex "1.2.7"
  :description "Simple fast library that compress, encrypt and encode data, for session storage and other use-cases"
  :url "https://github.com/gerritjvv/codex"
  :license {:name "Apache License 2.0"
            :url "https://github.com/gerritjvv/codex/blob/master/LICENSE"}

  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]

  :global-vars {*warn-on-reflection* true
                *assert*             false}


  :javac-options     ["-target" "1.8" "-source" "1.8"]
  :dependencies [[org.clojure/clojure "1.10.0-alpha6"]
                 [com.github.gerritjvv/encode-core "1.2.2"]
                 [joda-time/joda-time "2.10.3"]]

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]])
