%For each of these tests, replace the 'horners_yourlastname' with the name
%of your function.  
%
%In the last example, follow the instructions of how to
%create a random polynomial. 
%
%Once executed, this test file will produce a.txt file within 
%your Current Directory that you are to include with your
%report and source code when turned into HARVEY. The name of this diary
%file is determined below on line 18.
%
%YOU ARE ONLY TO ALTER LINES CONTAINING THE ARROWS: '<<----'. 
%DO NOT CHANGE ANY OTHER CONTENTS OF THIS FILE.

clear all;
close all;

diary wu_testFile; %<<----Replace 'yourlastname' with your last name.

a0 = [3.5713 10.714 2.1426 6.428 7.2158 1.3379];
x0 = 6.39338;

t0 = horner_wu(a0, x0, 0)%<<----
t0d = horner_wu(a0, x0, 1)%<<----


a1 = [3 5 -4 0 7 3];
x1 = -2;

t1 = horner_wu(a1, x1, 0)%<<----
t1d = horner_wu(a1, x1, 1)%<<----

a2 = [17 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1];
x2 = 9.75;

t2 = horner_wu(a2, x2, 0)%<<----
t2d = horner_wu(a2, x2, 1)%<<----

a3 = [-900.5 0.0008 20000 -7 ];
x3 = 7.4;

t3 = horner_wu(a3, x3, 0)%<<----
t3d = horner_wu(a3, x3, 1)%<<----

%For the last example, replace 'EmailNumbersHere' with the numbers that
%show up in your TU email. For example, if your TU email was
%abc7384@utulsa.edu, then you would enter
%
%rng(7384); 
%
%The remaining commands and instructions should not be changed.

rng(4111);%<<----
a4 = rand([1 10]);
x4 = rand();

t4 = horner_wu(a4, x4, 0)%<<----
t4d = horner_wu(a4, x4, 1)%<<----


diary off;
