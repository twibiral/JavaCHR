# JavaCHR
A modern Object-oriented implementation of Constraint Handling Rules in Java.

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

### RuleApplicator
An algorithm that applies ``Rule``'s to a set of ``Constraint``'s. Takes some ``Rules``'s as varargs.

### Rule
Rule consist of guard and body, the number of constraints in the head(s) is specified in the constructor.
The ``.guard()`` method gets a lambda with boolean as return type as parameter and the ``body()`` method gets a lambda 
without return type (void) as parameter. More information can be found in the Body or the Guard interface.

The head of the rule is defined 

### Constraint
A generic class which holds a value that is specified via the constructor. The ``get()`` method returns the internally stored object.

### Example
Rules can be created by instantiating the right rule object and setting guard and body with lambda functions. Greatest Common Divisor example:

```java
Rule rule1 = new Simplification(1 /* The number of constraints in the head */)   
        .guard(head -> (int) head[0].get() == 0) // head is an array with constraints  
        .body((head, newConstraints) -> {/* empty body in Simplification just deletes the head */});  
 
Rule rule2 = new Simpagation(1 /* n constraints in head1 */, 1 /* n constraints in head2 */ )   
        .guard((head1, head2) -> (int) h1[0].get() > 0 && (int) h1[0].get() <= (int) h2[0].get())
        .body((head1, head2, newConstraints) -> {   
            int n = (int) x1[0].get();   
            int m = (int) x2[0].get();   
            newConstraints.add(m % n);    
            // All constraints that should be added to the other constraints are added to this list.
        });
```

or:

```java
Rule rule1 = new Simplification(Integer.class)   
        .guard(head -> (int) head[0].get() == 0) // head is an array with constraints 
        .body((head, newConstraints) -> {/* empty body in Simplification just deletes the head */});  
 
Rule rule2 = new Simpagation(1 /* n constraints in head1 */, Integer.class, Integer.class)   
        .guard((head1, head2) -> (int) h1[0].get() > 0 && (int) h1[0].get() <= (int) h2[0].get())
        .body((head1, head2, newConstraints) -> {   
            int n = (int) x1[0].get();   
            int m = (int) x2[0].get();   
            newConstraints.add(m % n);    
            // All constraints that should be added to the other constraints are added to this list.
        });
```

Now we can create a ``RuleApplicator`` with the rules:
````java
RuleApplicator applicator = new SimpleRuleApplictaor(rule1, rule2);
````

Apply the rule to constraints. Calculating the greatest common divisor of some numbers:
````java


RuleApplicator result = applicator.execute(42, 1337, 8080);
System.out.println(result);
````

``applicator.execute(...)`` returns a List of constraints.

More examples can be found in the [examples package](/src/main/java/javachr/examples) and in the [tests](/src/test/javachr/). The package [webserver](/src/main/java/javachr/examples/webserver) contains a https webserver implemented with CHR rules.


## Use as local library
To use the library you can download the JavaCHR-<version>.jar file and use it as library for your project.
Here is how to use it for [gradle](https://appmediation.com/how-to-add-local-libraries-to-gradle/) and for 
[IntelliJ](https://stackoverflow.com/questions/1051640/correct-way-to-add-external-jars-lib-jar-to-an-intellij-idea-project).


## Future Work
- [ ] Union Types for the Head objects
- [ ] Hashing in Constraint Store
- [ ] RuleApplicator with Refined Operational Semantics
        

#### Contact: tim.wibiral@uni-ulm.de
