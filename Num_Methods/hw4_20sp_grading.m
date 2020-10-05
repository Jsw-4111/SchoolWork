%For each of these tests, replace the 'yourlastname_trap' or 
%'yourlastname_simp' with the name of your appropriate function.  
%
%This file is designed to catch and display the output errors (where 
%appropriate) that should be generated from your fucntion without halting 
%the execution of the other tests. 
%
%Once executed, this test file will produce a .txt file within 
%your Current Directory that you are to include with your
%report and source code when turned into HARVEY. The name of this diary
%file is determined below on line 20.
%
%YOU ARE ONLY TO ALTER LINES CONTAINING THE ARROWS: '<<----'. 
%DO NOT CHANGE ANY OTHER CONTENTS OF THIS FILE.


close all;
clear all;

diary wu_HW4GradeFile %<<----Replace 'yourlastname' with your last name.

    disp('FIRST')
        q1 =  wu_trap(@(x) 0.2 + 25*x - 200*x^2 + 675*x^3 - 900*x^4 + 400*x^5, 0, 0.8, 1)%<<----

    disp('FIRST Simpson')
        qs1 = wu_simp(@(x) 0.2 + 25*x - 200*x^2 + 675*x^3 - 900*x^4 + 400*x^5, 0, 0.8, 2)%<<----


    disp('SECOND')
        q2 = wu_trap(@(x) 0.2 + 25*x - 200*x^2 + 675*x^3 - 900*x^4 + 400*x^5, 0, 0.8,4) %<<----

    disp('SECOND Simpson')
        qs2 = wu_simp(@(x) 0.2 + 25*x - 200*x^2 + 675*x^3 - 900*x^4 + 400*x^5, 0, 0.8, 4) %<<----


    disp('THIRD')
         q3 = wu_trap(@(x) 1/x, 1, 2, 5) %<<----
 try
    disp('THIRD Simpson')
        q3 = wu_simp(@(x) 1/x, 1, 2, 5) %<<----
    catch ME
        display(ME.message)
 end
 
%For the last examples, replace 'EmailNumbersHere' with the numbers that
%show up in your TU email. For example, if your TU email was
%abc7384@utulsa.edu, then you would enter
%
%rng(7384); 
%
%The remaining commands and instructions should not be changed.
 
 rng(4111); %<<----
 r_const = rand([1 5]);
 
 format long
 
     disp('FOURTH')
        q4 = wu_trap(@(x) r_const(1)*x + r_const(2), r_const(3),r_const(4), 1) %<<----
    
     disp('FOURTH Simpson')
        qs4 = wu_simp(@(x) r_const(1)*x + r_const(2), r_const(3),r_const(4), 2)%<<----
        
     syms ex; 
        true4 = double(int(r_const(1)*ex + r_const(2), r_const(3),r_const(4)))
        

    disp('FIFTH')
        q5 = wu_trap(@(x) r_const(5)*x^2 + r_const(1)*x + r_const(2), r_const(3),r_const(4), 1)%<<----
  
    disp('FIFTH Simpson')
        q5 = wu_simp(@(x) r_const(5)*x^2 + r_const(1)*x + r_const(2), r_const(3),r_const(4), 2) %<<----
        
        true5 = double(int(r_const(5)*ex^2 + r_const(1)*ex + r_const(2), r_const(3),r_const(4)))
        
 format short

 diary off