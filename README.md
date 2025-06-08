# Gitter 🛠️

**Gitter** is a lightweight version control system inspired by Git.  
It mimics the core functionality of Git, implemented from scratch for educational and practical purposes.

---

## Features

Gitter currently supports the following commands:

- `gitter init` — Initialize a new Gitter repository
- `gitter add <file>` — Add files to the staging area
- `gitter commit -m "<message>"` — Commit changes with a message
- `gitter status` — Show the working tree status
- `gitter log` — View commit history
- `gitter diff` — Show changes between working directory and last commit
- `gitter reset` — Unstage files or reset to a previous commit
- `gitter help` — List available commands and usage

Each command mimics the behavior of its Git counterpart to provide a familiar experience.


## Setting up

1. clone the repository
2. run `mvn clean install`
3. replace '<path to jar file>' with the actual jar location of the build
4. copy 'gitter.sh' file to classpath
5. give the file execution permission via `sudo chmod +x <path to file>`

Now you can use gitter just like git command.


## Objectives

1. Understand how Git works internally
2. Learn core concepts of version control
3. Build your own version control tools
4. Great for educational and academic projects


## Future Enhancements

- Branching and merging
- Stashing
- Remote repository simulation
- Aliases and command chaining
- Persistent log format improvements

