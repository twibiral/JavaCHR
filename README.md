# JavaCHR
Object-oriented implementation of Constraint Handling Rules in Java.

### Constraint Handling Rules (CHR)
CHR was created by Thom Frühwirth in 1991 and consists of constraints and rules that are applied to them. There are 3 types of rules:

- Propagation: Head ==> \[Guard | \] Body.
- Simplification: Head <=> \[Guard | \] Body.
- Simpagation: Head1 \ Head2 <=> \[Guard | \] Body.

Head and body can each consist of several constraints. The Guard is the admission requirement for the constraints from the Head.
The constraints from the body are added to the existing set of constraints. During the simplification the complete head is removed. With the Simplification only the Head2. During propagation the constraints of the head remain in the original set. 
More information about CHR can be found in [Wikipedia](), in the [Prolog documentation]() and in this [paper]().

### JavaCHR
This project is an object-oriented implementation of CHR only in Java without extra compilation. That makes it possible to use Java objects in the CHR program.
Some algorithms are really short or really fast when implemented in CHR, e.g. its possible to create a non-recursive greatest common divisor (GCD) algorithm that computes the GCD of a any quantity of numbers. 
Or you can use some CHR rules for an ai in a game, use the rules just to check if some special conditions are met for some objects, reason from data or analyse text or lexicographic tokens.
With Java the CHR rules can not be just used for primitive data types but also for very complex objects and data.

### How to use
Rules can be created by instantiating the right rule object and setting guard and body with lambda functions. Greatest Common Divisor example:

`
Rule rule 1 = new Simplification(1 /* The number of constraints in the head */)   
.guard(head -> (int) head[0].value() == 0) // head is an array with constraints  
.body((head, newConstraints) -> {/* empty body in Simplification just deletes the head */});   
Rule rule2 = new Simpagation(1 /* n constraints in head1 */, 1 /* n constraints in head2 */ )   
.guard(   
        (head1, head2) -> (int) h1[0].value() > 0 && (int) h1[0].value() <= (int) h2[0].value()    
).body(   
        (head1, head2, newConstraints) -> {   
            int n = (int) x1[0].value();   
            int m = (int) x2[0].value();   
            newConstraints.add(new Constraint<>(m % n));    
            // All constraints that should be added to the other constraints is added to this list.   
        }   
);`



### Use as local library
To use the library you can download the JavaCHR-X.X.jar file and use it as library for your project.
Here is how to use it for [gradle](https://appmediation.com/how-to-add-local-libraries-to-gradle/) and for 
[IntelliJ](https://stackoverflow.com/questions/1051640/correct-way-to-add-external-jars-lib-jar-to-an-intellij-idea-project).

