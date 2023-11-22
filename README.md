define start(a, b, d : integer; c, e : real)
constants
pi = 3.14
test = 10
myName = "michael"
lastName = "paglia"
welcome = "welcome to my program"
char = 'c'
variables
a, b, d : integer; c, e : real; f, madeBy : string; bool : boolean
begin
write welcome
madeBy := "made by " + myName + " " + lastName
write madeBy
(* ASSIGNMENTS *)
(* Tests boolean and writes char because bool is set to true *)
bool := true
if bool = true then
begin
write char
end
elsif bool = false then
begin
write myName
end
(* Tests boolean with a while loop and writes paglia one time because boolean is false *)
while bool = true
begin
write lastName
bool := false
end
(* Tests assignment of a to int 5 *)
a := 5
write var a
(* CONDITIONALS *)
(* Tests while loop changing a to int 100 *)
while a < 10
begin
a := 11
end
(* User-defined function, prints 104 because 100 plus 4 *)
add a, b, d
write var d
(* Tests if block, prints FloatDataType 0, then FloatDataType 6.9 *)
if c > 3.14 then
begin
write var c
end
elsif c = 3.14 then
begin
c := 4.20
write var c
end
else
begin
c := 6.90
write var c
end
(* Tests subtract function, 10 minus 7 is 3 *)
subtract a, c, d
write var d
(* Tests for loop, writes incremented values of b 9, 14, 19, 24 *)
(* b = 4 *)
b := 4
for b from 1 to 5
begin
b := (b + 5)
write var b
end
(* BUILT-INS *)
(* Prints michael *)
write myName
substring myName, 0, 4, var f
(* Prints mich *)
write f
(* Prints ael *)
left myName, 4, var f
write f
(* Prints el *)
right myName, 2, var f
write f
(* Writes michaelpaglia *)
f := myName + lastName
write f
(* Prints 5 *)
squareRoot 25.0, var c
write var c
(* Prints a random integer with an upper bound of 100 *)
getRandom var a
write var a
(* Reads var a and var c *)
read var a, var c
(* Prints 10.0 *)
intToReal test, var e
write var e
(* Prints 3, updates value of var b to 3 *)
realToInt pi, var b
write var b
getRandom var a
(* Prints a new random integer, updates value of var a *)
write var a
multiply a, b, d
(* Prints the new random integer for a, 3 for b, 5.0 for c, 10.0 for  d *)
write var a, var b, var c, var d
end
(* User defined function to add two numbers *)
define add(a, b, d : integer)
begin
d := a + b
end
(* User defined function to subtract two numbers *)
define subtract (a, b, d : integer)
begin
d := a - b
end
(* User defined function to multiply two numbers *)
define multiply (a, b, d : integer)
begin
d := a * b
end
