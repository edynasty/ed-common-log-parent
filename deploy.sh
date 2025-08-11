export GPG_TTY=$(tty)
./mvnw -DskipTests=true deploy -X -P central