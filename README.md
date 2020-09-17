# JavaCHR
Object-oriented implementation of Constraint Handling Rules in Java.

Here you find the [JavaDoc](docs/index.html) and a short introduction follows below.

### Constraint Handling Rules (CHR)
CHR was created by Thom Frühwirth in 1991 and consists of constraints and rules that are applied to them. There are 3 types of rules:

- Propagation: Head ==> \[Guard | \] Body.
- Simplification: Head <=> \[Guard | \] Body.
- Simpagation: Head1 \ Head2 <=> \[Guard | \] Body.

Head and body can each consist of several constraints. The Guard is the admission requirement for the constraints from the Head.
The constraints from the body get added to the existing set of constraints. A Simplification removes the complete head. The Simpagation removes only Head2 and Propagation keeps the constraints of the Head in the original set. 
More information about CHR can be found in [Wikipedia](https://en.wikipedia.org/wiki/Constraint_Handling_Rules), in the [Prolog documentation](https://www.swi-prolog.org/man/chr.html) and in this [introduction](https://www.informatik.uni-ulm.de/pm/fileadmin/pm/home/fruehwirth/Papers/chr-lnai08.pdf).

### JavaCHR
This project is an object-oriented implementation of CHR only in Java without extra compilation. That makes it possible to use Java objects in the CHR program.
Some algorithms are really short or really fast when implemented in CHR, e.g. its possible to create a non-recursive greatest common divisor (GCD) algorithm that computes the GCD of a any quantity of numbers. 
Or you can use some CHR rules for an ai in a game, use the rules just to check if some special conditions are met for some objects, reason from data or analyse text or lexicographic tokens.
With Java the CHR rules can not be just used for primitive data types but also for very complex objects and data.

## How to use

### ConstraintHandler
An algorithm that applies ``Rule``'s to a set of ``Constraint``'s. Takes some ``Rules``'s as varargs.

### Rule
Rule consist of guard and body, the number of constraints in the head(s) is specified in the constructor.
The ``.head()`` method gets a lambda with boolean as return type as parameter and the ``body()`` method gets a lambda 
without return type (void) as parameter. More information can be found in the Body or the Guard interface.

### Constraint
A generic class which holds a value that is specified via the constructor. The ``.equals(Object)`` method is 
created so that its possible to compare a ``Constraint<T>`` object with a ``T`` directly:
````java
Constraint<String> stringConstraint = new Constraint<>("Hello World!");
stringConstraint.equals("Hello World!");
````
Here the Comparison return true.

To access the content of a value the ```.value()``` method is used.

### Example
Rules can be created by instantiating the right rule object and setting guard and body with lambda functions. Greatest Common Divisor example:

```java
Rule rule1 = new Simplification(1 /* The number of constraints in the head */)   
        .guard(head -> (int) head[0].value() == 0) // head is an array with constraints  
        .body((head, newConstraints) -> {/* empty body in Simplification just deletes the head */});  
 
Rule rule2 = new Simpagation(1 /* n constraints in head1 */, 1 /* n constraints in head2 */ )   
        .guard((head1, head2) -> (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value())    
        .body((head1, head2, newConstraints) -> {   
            int n = (int) x1[0].value();   
            int m = (int) x2[0].value();   
            newConstraints.add(new Constraint<>(m % n));    
            // All constraints that should be added to the other constraints is added to this list.   
        });
```

Now we can create a ConstraintHandler with the rules:
````java
ConstraintHandler handler = new SimpleHandler(rule1, rule2);  // SimpleHandler is the basic single threaded handler.
````
It's also possible to add the Rules with `handler.add(rule)` to an existing ConstraintHandler. An arbitrary number of rules can be used.   

Now let's compute the greatest common divisor from a set of numbers. `ConstraintHandler.solve()` can take 
a ``ConstraintStore`` that contains some ``Constraint``s or directly an array of generic type ``T`` as varargs:
````java
ConstraintStore store = new ConstraintStore();
store.add(new Constraint<>(42));
store.add(new Constraint<>(1337));
// ...
store.add(new Constraint<>(8080));

ConstraintStore result = handler.solve(store);
System.out.println(result);
````
or
````java
ConstraintStore result = handler.solve(42, 1337, 8080 /* , ... */);
System.out.println(result);
````

``handler.solve()`` returns a ``ConstraintStore`` that contains all ``Constraint``s that are in the result.

More examples can be found in the [examples package](/src/main/java/wibiral/tim/javachr/examples) and in the [tests](/src/test/java/wibiral/tim/javachr/).

### Parallel Execution
CHR is highly parallelizable what can give the execution a huge speedup. Handlers can only be parallelized if the order in which rules are applied makes no difference. This is for example the case with GCD and finding the maximum of a set of numbers.
To use semi-parallel execution you must use the ``SemiParallelHandler`` instead of the ``SimpleHandler``. The additional constructor parameter determines the number of Threads to use.
In the ``SemiParallelHandler`` just the applying of the Rules is parallel, the matching of rules and constraint is executed sequentially.
When the Handler isn't needed anymore you must kill ist with the ``.kill()`` method. The Handler is not using computation resources while waiting 
but holds Thread objects so they must not created every time when the Handler gets constraints to solve.

```java
SemiParallelHandler handler = new SemiParallelHandler(1, rules);
handler.solve(constraints);
handler.kill();
```

### Use as local library
To use the library you can download the JavaCHR-X.X.jar file and use it as library for your project.
Here is how to use it for [gradle](https://appmediation.com/how-to-add-local-libraries-to-gradle/) and for 
[IntelliJ](https://stackoverflow.com/questions/1051640/correct-way-to-add-external-jars-lib-jar-to-an-intellij-idea-project).
