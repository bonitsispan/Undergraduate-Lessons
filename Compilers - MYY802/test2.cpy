#$ Mponitsis Pantelis    AM: 4742    Username: cse94742 #$
#$ Sidiropoulos Georgios AM: 4789    Username: cse94789 #$

#$ This test should print the following numbers on the Risc-V window #$

#$ 0 #$
#$ 5 #$
#$ 5 #$
#$ 4 #$
#$   #$
#$ 14 #$
#$ 8 #$
#$ 10 #$
#$ 14 #$

def main_f1():
#{
    #declare a, b, c

    def f(x, y):
    #{
        y = x - 1;
        c = 10;
        return(y);
    #}

    a = 5;
    b = 5;
    print(c);
    c = f(a, b);
    print(a);
    print(b);
    print(c);
#}

def main_f2():
#{
    #declare a, b, c, d

    def f1(x, y):
        #{

           def f3():
                #{
                    b = a;
                    a = x;
                    return(a);
                #}
            
            a = f3() + y;
           
            if(a > 10):
                c = a - 4;
            
            return(a);
        #}
    
    a = 8;
    b = 1;
    c = 2;

    d = f1(4, 10);
    print(a);
    print(b);
    print(c);
    print(d);
    
#}
   
if __name__=="__main__":
    main_f1();
    main_f2();