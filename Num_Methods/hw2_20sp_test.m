%For each of these tests, replace the 'bisect_yourlastname' or 
%'secant_yourlastname' with the name of your appropriate function.  
%
%This file is designed to catch and display the output errors that should
%be generated from your function without halting the execution of the other 
%tests. 
%
%Once executed, this test file will produce a .txt file within 
%your Current Directory that you are to include with your
%report and source code when turned into HARVEY. The name of this diary
%file is determined below.
%
%YOU ARE ONLY TO ALTER LINES CONTAINING THE ARROWS: '<<----'. 
%DO NOT CHANGE ANY OTHER CONTENTS OF THIS FILE.


close all;
clear all;

diary Wu_Hw2TestFile; %<<----Replace 'yourlastname' with your last name.

    disp('FIRST')
try
        [n0, l0, r0, c0] =  bisect_Wu(@(x) x^16 - 1, 0, 1.5,1e-15,10)%<<----
    catch ME
        display(ME.message)
end
 
 try
    disp('FIRST Secant')
        [ns0, rs0] = secant_Wu(@(x) x^16 - 1,0, 2, 1e-15, 30)%<<----
    catch ME
        display(ME.message)
 end

    disp('SECOND')
try
        [n1, l1, r1, c1] = bisect_Wu(@(x) cos(x) - x, 0, 0.5,1e-7,30) %<<----
    catch ME
        display(ME.message)
end

try
    disp('SECOND Secant')
        [ns1, rs1] = secant_Wu(@(x) sin(x), pi/4.0,3*pi/4.0, 1e-7, 30) %<<----
    catch ME
        display(ME.message)
end

    disp('THIRD')
[n2, l2, r2, c2] = bisect_Wu(@(x) 1/x, -1, 1, 1e-7, 30) %<<----
 try
    disp('THIRD Secant')
        [ns2, rs2] = secant_Wu(@(x) 1/x, -1, 1, 1e-7, 30) %<<----
    catch ME
        display(ME.message)
 end

    disp('FOURTH')
[n3, l3, r3, c3] = bisect_Wu(@(x) x^3 + 4*x^2 - 10, 1, 2, 1e-4, 15) %<<----
    
    disp('FOURTH Secant')
[ns3, rs3] = secant_Wu(@(x) x^3 + 4*x^2 - 10, 0.5, 1, 1e-4, 15)%<<----


    disp('FIFTH')
[n4, l4, r4, c4] = bisect_Wu(@(x) x^5 + x^3  + 3, -2, 0, 1e-5, 30)%<<----
  
    disp('FIFTH Secant')
[ns4, rs4] = secant_Wu(@(x) x^5 + x^3  + 3, -1, 1, 1e-5, 60) %<<----

diary off
