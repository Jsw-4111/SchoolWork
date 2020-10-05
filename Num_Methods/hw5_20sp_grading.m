%For each of these tests, replace the 'yourlastname_euler' or 
%'yourlastname_rk4' with the name of your appropriate function.  
%
%Once executed, this test file will produce a .txt file within 
%your Current Directory that you are to include with your
%report and source code when turned into HARVEY. The name of this diary
%file is determined below on line 16.
%
%YOU ARE ONLY TO ALTER LINES CONTAINING THE ARROWS: '<<----'. 
%DO NOT CHANGE ANY OTHER CONTENTS OF THIS FILE.


close all;
clear all;

diary wu_HW5GradeFile %<<----Replace 'yourlastname' with your last name.

    disp('First')
        y1 =  wu_euler(@(x,y) 2, 1.5, 4, 7, 5);%<<----
        rk4_1 = wu_rk4(@(x,y) 2, 1.5, 4, 7, 5);%<<----
        
        ex1 = 1.5:(4 - 1.5)/5:4;
        why1 = 2*ex1 + 4;
        
        euler_err1 = norm(y1 - why1, Inf)
        rk4_err1 = norm(rk4_1 - why1, Inf)
        
    disp('Second')
        y2 =  wu_euler(@(x,y) -3.5*x + 17, 0, 2, -2, 4);%<<----
        rk4_2 = wu_rk4(@(x,y) -3.5*x + 17, 0, 2, -2, 4);%<<----
        
        ex2 = 0:(2 - 0)/4:2;
        why2 = (-3.5/2)*ex2.^2 + 17*ex2 - 2;
        
        euler_err2 = norm(y2 - why2, Inf)
        rk4_err2 = norm(rk4_2 - why2, Inf)
        
    disp('Third')
        y3 =  wu_euler(@(x,y) -2*x^3 + 12*x^2 - 20*x + 8.5, 0, 3, 1, 6);%<<----
        rk4_3 = wu_rk4(@(x,y) -2*x^3 + 12*x^2 - 20*x + 8.5, 0, 3, 1, 6);%<<----
        
        ex3 = 0:(3 - 0)/6:3;
        why3 = -0.5*ex3.^4 + 4*ex3.^3 - 10*ex3.^2 + 8.5*ex3 + 1;
        
        euler_err3 = norm(y3 - why3, Inf)
        rk4_err3 = norm(rk4_3 - why3, Inf)
       
    format long
        
    disp('Fourth')
        y4 =  wu_euler(@(x,y) -2*x - y, 0, 0.4, -1, 4);%<<----
        rk4_4 = wu_rk4(@(x,y)  -2*x - y, 0, 0.4, -1, 4);%<<----
        
        ex4 = 0:(0.4 - 0)/4:0.4;
        why4 =-3*exp(-ex4) - 2*ex4 + 2;
        
      
        euler_err4 = norm(y4 - why4, Inf)
        rk4_err4 = norm(rk4_4 - why4, Inf)
        
     disp('Fifth')
        y5 =  wu_euler(@(x,y) -1000*y + 3000 - 2000*exp(-x), 0, 0.006, 0, 4);%<<----
        rk4_5 = wu_rk4(@(x,y)  -1000*y + 3000 - 2000*exp(-x), 0, 0.006, 0, 4);%<<----
        
        ex5 = 0:(0.006 - 0)/4:0.006;
        why5 =3 - 0.998*exp(-1000*ex5) - 2.002*exp(-ex5);
        
      
        euler_err5 = norm(y5 - why5, Inf)
        rk4_err5 = norm(rk4_5 - why5, Inf)

    format short
    
diary off