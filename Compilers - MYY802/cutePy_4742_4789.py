# Mponitsis Pantelis    AM: 4742    Username: cse94742
# Sidiropoulos Georgios AM: 4789    Username: cse94789


import sys
#start kai endiameses katastaseis:

#define state0 0
#define state1 1
#define state2 2
#define state3 3
#define state4 4
#define state5 5
#define state6 6
#define state7 7
#define state8 8
#define state9 9
#define state10 10

#telikes katastaseis:

#define id 11
#define number 12
#define addOperator 13
#define mulOperator 14
#define assignement 15
#define relOperator 16
#define delimeter 17
#define groupSymbol 18
#define eof 19
#define error 20

#define white_character 0
#define char 1
#define digit 2
#define underscore 3
#define  + 4
#define -  5
#define * 6
#define / 7
#define = 8
#define < 9
#define > 10
#define  ! 11
#define  ;  12
#define  , 13
#define : 14
#define [ 15
#define ] 16
#define ( 17
#define ) 18
#define  { 19
#define  }  20
#define # 21
#define  $ 22
#define eof 23
#define " 24

myKeywords = ["def","if","else","while","return","print","int","input","not","and","or","declare","program"]

myArray = [["0","1","2","11","3","mulOperator","4","5","6","7","delimiter","groupSymbol","error","8","error","eof","Main"],
        ["id","1","1","1","id","id","id","id","id","id","id","id","id","id","id","id","id"],
        ["number","number","2","number","number","number","number","number","number","number","number","number","number","number","number","number","number"],
        ["addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator","addOperator"],
        ["error","error","error","error","error","error","mulOperator","error","error","error","error","error","error","error","error","error","error"],
        ["assignement","assignement","assignement","assignement","assignement","assignement","assignement","relOperator","assignement","assignement","assignement","assignement","assignement","assignement","assignement","assignement","assignement"],
        ["relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator","relOperator"],
        ["error","error","error","error","error","error","error","relOperator","error","error","error","error","error","error","error","error","error"],
        ["error","declare","error","error","error","error","error","error","error","error","error","error","groupSymbol","error","9","error","error"],
        ["9","9","9","9","9","9","9","9","9","9","9","9","9","10","9","error","9"],
        ["9","9","9","9","9","9","9","9","9","9","9","9","9","9","0","error","9"],
        ["underscore","underscore","underscore","Name","underscore","underscore","underscore","underscore","underscore","underscore","underscore","underscore","underscore","underscore","underscore","underscore","underscore"]]


################ Classes and functions for generating the final code. ################


def gnlvcode(variable_name):
    global output_code
    global fileOut
    searchResults = Symbol_Table.search(variable_name)
   
    t0 = "t0"  
    sp = "sp"  
    offset = searchResults[1]  
    level =  Symbol_Table.current_scope.level - searchResults[0]
    code = ""  

    code += f"lw {t0}, -4({sp})\n\t"

    for i in range(level-1):
        code += f"lw {t0}, -4({t0})\n\t"

    code += f"addi {t0}, {t0}, -{offset}\n\t"
    output_code.append(code)
    return 

def loadvr(v, r):
    global output_code
    global fileOut
    code =""
    sp = "sp"
    gp = "gp"
    t0 = "t0"
    if str(v).isdigit():           ####
        code = f"li {r}, {v}\n\t"
        output_code.append(code)
    else:
        v_information = Symbol_Table.searchLoad(v)  #v_information[scope, entity]
        if ((v_information[0] == 0) and (isinstance(v_information[1], Variable)) and (not v_information[1].name.startswith("T%"))):  
            code = f"lw {r},-{v_information[1].offset}({gp})\n\t"
            output_code.append(code)
        elif ((v_information[0] == Symbol_Table.current_scope.level) and ((isinstance(v_information[1], Variable)) or (isinstance(v_information[1], Parameter)))):
            code = f"lw {r},-{v_information[1].offset}({sp})\n\t"
            output_code.append(code)
        elif ((v_information[0] != Symbol_Table.current_scope.level) and ((isinstance(v_information[1], Variable)) or (isinstance(v_information[1], Parameter)))):  
            gnlvcode(v)
            code = f"lw {r}, ({t0})\n\t"
            output_code.append(code)
        else:
           sys.exit("Error: Not found name: "+str(v_information[1].name))
    
def storerv(r, v):
    global fileOut
    global output_code
    code =""
    sp = "sp"
    gp = "gp"
    t0 = "t0"
    if v.isdigit():
        loadvr(v, t0)
        storerv(t0, r)
    else:
        v_information = Symbol_Table.searchLoad(v)  #v_information[scope, entity]
        if ((v_information[0] == 0) and (isinstance(v_information[1], Variable))  and (not v_information[1].name.startswith("T%"))):  
            code = f"sw {r},-{v_information[1].offset}({gp})\n"
            output_code.append(code)
        elif ((v_information[0] == Symbol_Table.current_scope.level) and ((isinstance(v_information[1], Variable)) or (isinstance(v_information[1], Parameter)))):
            code = f"sw {r},-{v_information[1].offset}({sp})\n"
            output_code.append(code)
        elif ((v_information[0] != Symbol_Table.current_scope.level) and ((isinstance(v_information[1], Variable)) or (isinstance(v_information[1], Parameter)))): 
            gnlvcode(v)
            code = f"sw {r}, ({t0})\n"
            output_code.append(code)
        else:
           sys.exit("Error: Not found name: "+str(v_information[1].name))

def search_for_main(name):
    for entity in main_functions_table:
        if name == entity.name:
            return entity

def produce_finalCode():
    global fileOut
    global quad_list
    global pointer
    global output_code
    global positions
    global positions_par
    global line
    global flag_main
    framelength_functions = Symbol_Table.current_scope.frameLength
    current_level =  Symbol_Table.current_scope.level - 1
    par_counter = 0
    par_counter_check = 0 
    t0 = "t0"
    t1 = "t1"
    t2 = "t2"
    sp = "sp"
    fp = "fp"
    ra = "ra"
    gp = "gp"
    a7 = "a7"
    a0 = "a0"
    
    for num_quad in range(pointer, len(quad_list.quads)):
        quad = quad_list.quads[num_quad]
        output_code.append("L"+str(num_quad+100)+":\n\t")
        if(quad.operator == ":="):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            storerv(t1, quad.operand3)
        elif(quad.operator == "+"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("add "+str(t1)+", "+str(t1)+", "+str(t2)+"\n\t")
            storerv(t1, quad.operand3)
        elif(quad.operator == "-"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("sub "+str(t1)+", "+str(t1)+", "+str(t2)+"\n\t")
            storerv(t1, quad.operand3)
        elif(quad.operator == "*"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("mul "+str(t1)+", "+str(t1)+", "+str(t2)+"\n\t")
            storerv(t1, quad.operand3)
        elif(quad.operator == "//"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("div "+str(t1)+", "+str(t1)+", "+str(t2)+"\n\t")
            storerv(t1, quad.operand3)
        elif(quad.operator == "jump"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            output_code.append("j "+"L"+str(quad.operand3)+"\n")
        elif(quad.operator == "=="):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("beq "+str(t1)+", "+str(t2)+", "+"L"+str(quad.operand3)+"\n")
        elif(quad.operator == "!="):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("bne "+str(t1)+", "+str(t2)+", "+"L"+str(quad.operand3)+"\n")
        elif(quad.operator == "<"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("blt "+str(t1)+", "+str(t2)+", "+"L"+str(quad.operand3)+"\n")
        elif(quad.operator == ">"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("bgt "+str(t1)+", "+str(t2)+", "+"L"+str(quad.operand3)+"\n")
        elif(quad.operator == "<="):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("ble "+str(t1)+", "+str(t2)+", "+"L"+str(quad.operand3)+"\n")
        elif(quad.operator == ">="):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1, t1)
            loadvr(quad.operand2, t2)
            output_code.append("bge "+str(t1)+", "+str(t2)+", "+"L"+str(quad.operand3)+"\n")
        elif(quad.operator == "ret"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1,t1)
            output_code.append("lw "+str(t0)+", -8("+str(sp)+")\n\t")
            output_code.append("sw "+str(t1)+", ("+str(t0)+")\n\t")
            output_code.append("lw "+str(ra)+", "+"("+str(sp)+")\n\t")
            output_code.append("jr ra\n")
        elif(quad.operator == "par" and quad.operand2 == "cv"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            if(par_counter == 0):
                output_code.append("addi "+str(fp)+", "+str(sp)+", -"+"\n\t")
                positions_par.append(len(output_code)-1)
            loadvr(quad.operand1, t0)
            number = 12+(4*par_counter)
            output_code.append("sw "+str(t0)+", -"+str(number)+"("+str(fp)+")\n")
            par_counter +=1
            par_counter_check +=1
        elif(quad.operator == "par" and quad.operand2 == "ret"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            if(par_counter == 0):
                output_code.append("addi "+str(fp)+", "+str(sp)+", -"+"\n\t")
                positions_par.append(len(output_code)-1)
            info_variable = Symbol_Table.searchLoad(quad.operand1)
            output_code.append("addi "+str(t0)+", "+str(sp)+", -"+str(info_variable[1].offset)+"\n\t")
            output_code.append("sw "+str(t0)+", -8("+str(fp)+")\n")
            par_counter = 0
        elif(quad.operator == "call" and quad.operand1.startswith("main_")): 
            if(flag_main):
                sys.exit("Error: Invalid name: "+str(quad.operand1))
            output_code.pop()
            main_function = search_for_main(quad.operand1)
            output_code.append("la "+str(a0)+", str_nl\n\t")
            output_code.append("li "+str(a7)+", 4\n\t")
            output_code.append("ecall\n\t")
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            output_code.append("jal ,"+"L"+str(main_function.start_quad)+"\n\n\t")

        elif (quad.operator == "call"):
            
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            function_informations = Symbol_Table.searchLoad(quad.operand1)

            if(par_counter_check != len(function_informations[1].FormalParameter_list)):
                sys.exit("Error: Invalid number of parameters"+" , function: "+str(function_informations[1].name))
            
            if(current_level <= function_informations[0]):
                if(current_level == function_informations[0]):
                    output_code.append("lw "+str(t0)+", -4("+str(sp)+")\n\t")
                    output_code.append("sw "+str(t0)+", -4("+str(fp)+")\n\t")
                else:
                    output_code.append("sw "+str(sp)+", -4("+str(fp)+")\n\t")
                output_code.append("addi "+str(sp)+", "+str(sp)+", "+str(function_informations[1].frame_length)+"\n\t")
                if(function_informations[1].frame_length == "-"):
                    positions.append(len(output_code)-1)
                else:
                    fillParFrameLength(function_informations[1].frame_length)
                output_code.append("jal L"+str(function_informations[1].start_quad)+"\n\t")
                output_code.append("addi "+str(sp)+", "+str(sp)+", -"+str(function_informations[1].frame_length)+"\n")
                if(function_informations[1].frame_length == "-"):
                    positions.append(len(output_code)-1)
            else:
                sys.exit("Error: Not found function"+" , line "+str(line))
            par_counter_check = 0
        elif(quad.operator == "begin_block"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            if(quad.operand1 == "main"): ###################################
                flag_main = 0
                output_code.pop(-2)
                output_code.append("Lmain:\n\t")
                output_code[-1], output_code[-2] = output_code[-2], output_code[-1]
                output_code.pop()
            elif(quad.operand1.startswith("main_")):
                output_code.append("addi "+str(sp)+", "+str(sp)+", "+str(search_for_main(quad.operand1).frame_length)+"\n\t")
                output_code.append("sw "+str(ra)+", ("+str(sp)+")\n\t")
                output_code.append("mv "+str(gp)+", "+str(sp)+"\n")
            else:
                output_code.append("sw "+str(ra)+", ("+str(sp)+")\n")
        elif(quad.operator == "end_block" and quad.operand1 == "main"):
            output_code.pop()
            continue
        elif(quad.operator == "end_block"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            if(quad.operand1.startswith("main_")):
                output_code.append("lw "+str(ra)+", ("+str(sp)+")\n\t")
                output_code.append("addi "+str(sp)+", "+str(sp)+", -"+str(search_for_main(quad.operand1).frame_length)+"\n\t")
                output_code.append("jr "+str(ra)+"\n")
            else:
                output_code.append("lw "+str(ra)+", ("+str(sp)+")\n\t")
                output_code.append("jr "+str(ra)+"\n")
        elif(quad.operator == "out"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            loadvr(quad.operand1,a0)
            output_code.append("li "+str(a7)+", 1\n\t")
            output_code.append("ecall\n\t")
            output_code.append("la "+str(a0)+", str_nl\n\t")
            output_code.append("li "+str(a7)+", 4\n\t")
            output_code.append("ecall\n")
        elif(quad.operator == "in"):
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            output_code.append("la "+str(a0)+", in\n\t")
            output_code.append("li "+str(a7)+", 4\n\t")
            output_code.append("ecall\n\t")
            output_code.append("li "+str(a7)+", 5\n\t")
            output_code.append("ecall\n\t")
            storerv(a0,quad.operand1)
        elif(quad.operator == "halt"):
            output_code.pop()
            output_code.append("#("+str(quad.operator)+", "+str(quad.operand1)+", "+str(quad.operand2)+", "+str(quad.operand3)+")\n\t")
            output_code.append("li "+str(a0)+", 0\n\t")
            output_code.append("li "+str(a7)+", 93\n\t")
            output_code.append("ecall\n\t")

        pointer += 1

    for i in range(0, len(positions)):
        last_position = output_code[positions[i]].rfind("-")
        output_code[positions[i]] = output_code[positions[i]][:last_position]+str(framelength_functions)+output_code[positions[i]][last_position+1:]

    fillParFrameLength(framelength_functions)
        
    positions.clear()
    for element in output_code:
        fileOut.write(element)
    output_code.clear()
    #par_counter = 0

def fillParFrameLength(framelenth):
    global positions_par

    for i in range(0, len(positions_par)):
        last_position = output_code[positions_par[i]].rfind("-")
        output_code[positions_par[i]] = output_code[positions_par[i]][:last_position]+str(framelenth)+output_code[positions_par[i]][last_position+1:]
    
    positions_par.clear()    


################ Classes and functions for generating intermediate code. ################


class Quad:
    def __init__(self,operator, operand1, operand2, operand3):
        self.operator = operator
        self.operand1 = operand1
        self.operand2 = operand2
        self.operand3 = operand3

    def __str__(self):
        return f'({self.operator}, {self.operand1}, {self.operand2}, {self.operand3})'
    
class ListOfQuads:
    def __init__(self):
        self.quads = []
    
    def addQuad(self, quad):
        self.quads.append(quad)
    
    def getQuadList(self):
        return self.quads
    
def genQuad(operator, operand1, operand2, operand3):
    global quad_number
    quad_number = quad_number + 1
    quad = Quad(operator, operand1, operand2, operand3)
    quad_list.addQuad(quad)
    return quad

def nextQuad():
    global quad_number
    return quad_number + 1

def newTemp():
    global newTempNumber
    newTempNumber = newTempNumber + 1
    return f"T%{newTempNumber}"

def emptyList():
    return []

def makeList(label):
    return [label]

def mergeList(list1, list2):
    return sorted(list1 + list2)

def backpatch(list, label):
    global quad_list
    for quad in list:
        quad_list.getQuadList()[quad-100].operand3 = label
    del list[:]


################ Classes and functions for the symbol table. ################ 


class Entity:
    def __init__(self,name):
        self.name = name

class Variable(Entity):
    def __init__(self, name, offset):
        Entity.__init__(self, name)
        self.offset = offset

class TempVariable(Variable):
    def __init__(self, name, offset):
        super().__init__(name, offset)

class Function(Entity):
    def __init__(self, name, start_quad, frame_length):
        super().__init__(name)
        self.start_quad = start_quad
        self.frame_length = frame_length
        self.FormalParameter_list = []

    def add_FormalParameter(self, FormalParameter):
        self.FormalParameter_list.append(FormalParameter)

    def update_info(self, start_quad, frame_length):
        self.start_quad = start_quad
        self.frame_length = frame_length

class FormalParameter(Entity):
    def __init__(self, name, mode):
        Entity.__init__(self, name)
        self.mode = mode
        
class Parameter(FormalParameter, Variable):
    def __init__(self, name, mode, offset):
        FormalParameter.__init__(self, name, mode)
        Variable.__init__(self, name, offset)
     
class Scope:
    def __init__(self, level):
        self.level = level
        self.Entity_list = []
        self.parent_scope = None
        self.frameLength = 12
        self.Return_check = 0

    def add_Entity(self, Entity):
        if not isinstance(Entity, Function):
            Entity.offset = self.frameLength
            self.Entity_list.append(Entity)
            self.frameLength += 4
        else:
            self.Entity_list.append(Entity)
    
    def remove_Entity(self):
        if self.parent_scope:
            self.Entity_list.clear()
    
    def update_function_info(self, start_quad, frame_length):
        for entity in reversed(self.Entity_list):
            if isinstance(entity, Function):
                entity.update_info(start_quad, frame_length)

class SymbolTable:
    def __init__(self):
        self.current_scope = None

    def add_scope(self):
        global level
        new_scope = Scope(level)
        level += 1
        if self.current_scope:
            new_scope.parent_scope = self.current_scope
        self.current_scope = new_scope
    
    def remove_scope(self):
        global level
        self.print_table()
        if level == 1 and (self.current_scope.Return_check > 0): #level 1 main functions
            sys.exit("Error: Main function has return statement"+" , line "+str(line))
        if level > 1 and (self.current_scope.Return_check == 0):
            sys.exit("Error: missing return statement in function"+" , line "+str(line))
        level -= 1
        if self.current_scope:
            if level > 0: 
                self.current_scope.parent_scope.Entity_list[-1].frame_length = self.current_scope.frameLength
            self.current_scope.remove_Entity()
            self.current_scope = self.current_scope.parent_scope

    def add_in_entity_list(self, entity):
        if self.current_scope:
            self.current_scope.add_Entity(entity)

    def add_formal_parameter(self, formal_parameter):
        if isinstance(self.current_scope.parent_scope.Entity_list[-1], Function):
            self.current_scope.parent_scope.Entity_list[-1].add_FormalParameter(formal_parameter)

    def lookup(self, name):#check for function Entity 
        if self.current_scope:
            current = self.current_scope
            while current:
                for entity in current.Entity_list:
                    if entity.name == name:
                        return 1
                current = current.parent_scope
        return 0 #not found 
    
    def lookup_for_dublicate_error(self, name, call_name):#check for dublicate
        if name in main_functions_names:
            return 1
        if self.current_scope:
            current = self.current_scope
            for entity in current.Entity_list:
                if entity.name == name :
                    return 1 #dublicate name
        if self.current_scope:
            current = self.current_scope
            while current:
                for entity in current.Entity_list:
                    if call_name == "declare":
                        if entity.name == name and (isinstance(entity, Function) or isinstance(entity, Parameter)):
                            return 1
                    else:
                        if entity.name == name and isinstance(entity, Function):
                            return 1
                current = current.parent_scope
        return 0 #unique name
    
    def look_up_FormalParameter(self,name):
        if self.current_scope:
            current = self.current_scope.parent_scope
            while current:
                for entity in current.Entity_list:
                    if entity.name == name and isinstance(entity, Parameter):
                            continue
                    elif entity.name == name:
                        return 1 #dublicate name 
                current = current.parent_scope
            return 0 #not found
    
    def update_fields(self, start_quad, frame_length):
        self.current_scope.parent_scope.update_function_info(start_quad, frame_length)
    
    def print_table(self):
        print("Symbol Table:")
        print("-------------")
        current_scope = self.current_scope
        while current_scope:
            print(f"Scope {current_scope.level}:")
            for entity in current_scope.Entity_list:
                if isinstance(entity, Parameter):
                    print(f"    Parameter {entity.name}: mode {entity.mode}, offset {entity.offset}")
                elif isinstance(entity, Function):
                    print(f"    Function {entity.name}: start_quad {entity.start_quad}, frame_length {entity.frame_length}")
                    for formal_parameter in entity.FormalParameter_list:
                        print(f"        Formal Parameter {formal_parameter.name}: parameter mode {formal_parameter.mode}")
                elif isinstance(entity, Variable):
                    print(f"    Variable {entity.name}: offset {entity.offset}")
            current_scope = current_scope.parent_scope
        print("\n\n");
    
######################################################################

    def search(self, name):
        if self.current_scope:
            current = self.current_scope.parent_scope
            while current:
                for entity in current.Entity_list:
                    if entity.name == name and isinstance(entity, Variable):
                        return [current.level, entity.offset]
                current = current.parent_scope
        sys.exit("Error: Not found name: "+str(name))
        return [0,0]
    
    def searchLoad(self, name):
        if self.current_scope:
            current = self.current_scope
            while current:
                for entity in current.Entity_list:
                    if entity.name == name:
                        return [current.level, entity]
                current = current.parent_scope
        sys.exit("Error: Not found name: "+str(name))


################ Declaration of global variables, functions of the lexical and syntax analyzers, and functions for the grammar rules of the language. ################ 


#fileOut = open("C://Users//Pantelis Bonitsis//Desktop//Uoi_Cse//4o//metafrastes//ex_3//deliverable//tests_results//test5_false.asm" , "w" )
fileOut = open("test_results.asm" , "w" )
line = 1
flag1 = 1
flag2 = 1
main_token = []
quad_number = 99
newTempNumber = 0
quad_list = ListOfQuads()
Symbol_Table = SymbolTable()
main_functions_table = []
main_functions_names = []
myFunctions = []
output_code = []
positions = []
positions_par = []
flag_main = 1
pointer = 0
level  = 0
lex_table = []

def make_first_lines_for_final_code():
    global fileOut

    fileOut.write('.data\nstr_nl: .asciz "\\n" \nin: .asciz "Give me input: "\n.text\n\n')
    fileOut.write("L_goToMain:\n\tj Lmain\n")
       
def lex():
    global myKeywords
    global myArray
    global file
    global Output
    global line
    global flag1
    global flag2
    global main_token
    global lex_table
    symbol = file.read(1).decode()
    count = 0
    if symbol == "":
        main_token = ["","eof",line]
        return
    state = "0"
    nextState = 0
    token = ""
    lenOftoken = 0
    while state.isdigit():
        if count  > 0:
            symbol = file.read(1).decode()
        if(symbol.isspace()):
            if(symbol == "\n"):
                line+=1
            nextState = 0
        elif(symbol.isalpha()):
            nextState = 1
        elif(str(symbol).isdigit()):
            nextState = 2
        elif(symbol == '_'):
            nextState = 3
        elif((symbol =='+') or (symbol =='-')):
            nextState = 4
        elif(symbol == '*'):
            nextState = 5
        elif(symbol == '/'):
            nextState = 6
        elif(symbol == '='):
            nextState = 7
        elif((symbol == '<') or (symbol == '>')):
            nextState = 8
        elif(symbol == '!'):
            nextState = 9
        elif((symbol ==';') or (symbol == ',') or (symbol ==':')):
            nextState = 10
        elif((symbol =='[') or (symbol == ']') or (symbol == '(')  or (symbol == ')')):
            nextState = 11
        elif((symbol == '{') or (symbol == '}')):
            nextState = 12
        elif(symbol == '#'):
            nextState = 13
        elif(symbol == '$'):
            nextState = 14
        elif(symbol == ""):
            nextState = 15
        else:
            nextState = 16
        state = myArray[int(state)][nextState]
        count = 1
        if state == "0" :
            token = ""
            lenOftoken = 0
            continue
        token += symbol
        lenOftoken += 1


    if state == "Name":
        token += file.read(6).decode()
        if token == "__name__":
            main_token = [token,'keyword',line]
            lex_table.append(str(main_token[0])+"          family: '"+str(main_token[1])+"' ,   line:  "+str(main_token[2]))
            return
        else:
            file.seek(-len(token),1)
            token = token[0]
            main_token = [token,'error',line]
            print("LexError: Token '"+token+"' , line "+str(line))
            sys.exit()

    if state == "Main":
        token += file.read(9).decode()
        if token == '"__main__"':
            main_token = [token,'keyword',line]
            lex_table.append(str(main_token[0])+"          family: '"+str(main_token[1])+"' ,   line:  "+str(main_token[2]))
            return
        else:
            file.seek(-len(token),1)
            token = token[0]
            main_token = [token,'error',line]
            print("LexError: Token '"+token+"' , line "+str(line))
            sys.exit()

    if state == "declare":
        token += file.read(6).decode()
        if token == "#declare":
            main_token = [token,'keyword',line]
            lex_table.append(str(main_token[0])+"          family: '"+str(main_token[1])+"' ,   line:  "+str(main_token[2]))
            return
        else:
            file.seek(-len(token),1)
            token = token[0]
            main_token = [token,'error',line]
            print("LexError: Token '"+token+"' , line "+str(line))
            sys.exit()
            return

    if (not state.isdigit()):
        if((state == "number") or (state=="id") or (state == "addOperator") or (state == "assignement") or(state == "error") ):
            token = token[:-1]
            if(state == "id"):
                if token in myKeywords:
                    state = "keyword"
                elif len(token) > 30:
                    print("LexError: Token '"+token+"' , line "+str(line) +" has more than 30 characters")
                    sys.exit()
            if((state == "number") and (int(token) > 2**32 - 1)):
                file.seek(-(len(token) + 2),1)
                if(file.read(1).decode() == "-"):
                    print("LexError: Token '-"+token+"' , line "+str(line) +" is not between -(2^32 - 1) and (2^32 - 1)")
                    sys.exit()
                file.read(1).decode()
                print("LexError: Token '"+token+"' , line "+str(line) +" is not between -(2^32 - 1) and (2^32 - 1)")
                sys.exit()
            file.seek(-1,1)
        if((state == "relOperator") and (token[-1] != "=") ):
            token = token[:-1]
            file.seek(-1,1)
        main_token = [token,state,line]
        if state == "error":
            print("LexError: Token '"+token+"' , line "+str(line))
            sys.exit()
    
    #print(str(main_token[0])+"          family: '"+str(main_token[1])+"' ,   line:  "+str(main_token[2]))
    lex_table.append(str(main_token[0])+"          family: '"+str(main_token[1])+"' ,   line:  "+str(main_token[2]))

def syntax_analyzer():
    global main_token
    make_first_lines_for_final_code()
    startRule()

def startRule():
    global main_token
    def_main_part()
    call_main_part()
    Symbol_Table.print_table()
    if main_token[0] == "":
        counter = 100;
        for quad in quad_list.getQuadList():
            print(str(counter)+": "+str(quad))
            counter += 1
        #sys.exit("\nThe Lexical and Syntactic analysis of the program finished successfully.\n")
        print("\nThe Lexical and Syntactic analysis of the program finished successfully.\n")
    else:
        sys.exit("SyntaxError: No characters are allowed after the end of the program")

def def_main_part():
    global fileOut
    global main_token
    global flag1
    flag1 = 1
    
    while(main_token[0] == "def"):
        flag1 = 0
        lex()
        tempDefName = main_token[0]
        def_main_function(tempDefName)

        Symbol_Table.remove_scope()
    if flag1:
        sys.exit("SyntaxError: Probably missing keyword 'def' or missing main functions"+" , line "+str(line))

def def_main_function(tempDefName):
    global main_token
    if main_token[1] == "id":
        if not (main_token[0].startswith("main_")):
            sys.exit("Error: Invalid main function name"+" , line "+str(line)) 
        if(main_token[0] in main_functions_names):
            sys.exit("Error: Dublicate main function name"+" , line "+str(line))
        myFunctions.append(main_token[0])
        main_functions_names.append(main_token[0])
        Symbol_Table.add_scope()
        lex()
        if main_token[0] == "(":
            lex()
            if main_token[0] == ")":
                lex()
                if main_token[0] == ":":
                    lex()
                    if main_token[0] == "#{":
                        lex()
                        declarations()
                        while(main_token[0] == "def"):
                            lex()
                            tempDefName2 = main_token[0]
                            def_function(tempDefName2)
                            Symbol_Table.remove_scope()
                        genQuad("begin_block", tempDefName, '_', '_')
                        entity = Function(tempDefName, quad_number, "_")
                        statements()
                        entity.frame_length = Symbol_Table.current_scope.frameLength
                        main_functions_table.append(entity)
                        genQuad("end_block", tempDefName, '_', '_')
                        produce_finalCode()
                        if main_token[0] == "#}":
                             lex()
                        else:
                            sys.exit("SyntaxError: '#}' was expected"+" , line "+str(line))
                    else:
                        sys.exit("SyntaxError: '#{' was expected"+" , line "+str(line))
                else:
                    sys.exit("SyntaxError: ':' was expected"+" , line "+str(line))
            else:
               sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: There is probably an illegal function name: "+main_token[0]+" or function name is missing"+" , line "+str(line))

def def_function(tempDefName2):
    global main_token
    if main_token[1] == "id":
        if (main_token[0].startswith("main_")):
            sys.exit("Error: Invalid function name"+" , line "+str(line)) 
        if(Symbol_Table.lookup(main_token[0]) == 1 or (main_token[0] in main_functions_names)):
            sys.exit("Error: Dublicate function name"+" , line "+str(line))
        entity = Function(main_token[0], "-", "-")
        myFunctions.append(main_token[0])
        Symbol_Table.add_in_entity_list(entity)
        Symbol_Table.add_scope()
        lex()
        if main_token[0] == "(":
            lex()
            temp = "function"
            id_list(temp)
            if main_token[0] == ")":
                lex()
                if main_token[0] == ":":
                    lex()
                    if main_token[0] == "#{":
                        lex()
                        declarations()
                        while(main_token[0] == "def"):
                            lex()
                            tempDefName3 = main_token[0]
                            def_function(tempDefName3)
                            Symbol_Table.remove_scope()
                        genQuad("begin_block", tempDefName2, '_', '_')
                        Symbol_Table.current_scope.parent_scope.Entity_list[-1].start_quad = quad_number
                        statements()
                        genQuad("end_block", tempDefName2, '_', '_')
                        produce_finalCode()
                        if main_token[0] == "#}":
                            lex()
                        else:
                            sys.exit("SyntaxError: '#}' was expected"+" , line "+str(line))
                    else:
                        sys.exit("SyntaxError: '#{' was expected"+" , line "+str(line))
                else:
                    sys.exit("SyntaxError: ':' was expected"+" , line "+str(line))
            else:
               sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: Τhere is probably an illegal fuction name "+main_token[0]+" , line "+str(line))

def declarations():
    global main_token
    while(main_token[0] == "#declare"):
        lex()
        temp = "declare"
        declaration_line(temp)

def declaration_line(temp):
    id_list(temp)

def statement():
    global main_token
    if((main_token[1] == "id") or (main_token[0] == "print") or (main_token[0] == "return")):
        simple_statement()
    elif((main_token[0] == "if") or (main_token[0] == "while")):
        structured_statement()
    else:
        sys.exit("SyntaxError: Μissing statements"+" , line "+str(line))

def statements():
    global main_token
    global flag2
    flag2 = 1
    while( (main_token[1] == "id") or (main_token[0] == "print") or (main_token[0] == "return") or (main_token[0] == "if") or (main_token[0] == "while")):
        statement()
        flag2 = 0
    if flag2:
        sys.exit("SyntaxError: Invalid statement syntax or missing statements"+" , line "+str(line))

def simple_statement():
    global main_token
    if(main_token[1] == "id"):
        tempId = main_token[0]
        lex()
        assignement_stat(tempId)
    elif(main_token[0] == "print"):
        lex()
        print_stat()
    elif(main_token[0] == "return"):
        lex()
        return_stat()

def structured_statement():
    global main_token
    if(main_token[0] == "if"):
        lex()
        if_stat()
    elif(main_token[0] == "while"):
        lex()
        while_stat()

def assignement_stat(tempid):
    global main_token
    id_temp = tempid
    if(main_token[0] == "="):
        lex()
        if((main_token[1] == "addOperator") or (main_token[1] == "number") or (main_token[0] == "(")  or (main_token[1] == "id") ):
            e = expression()
            if(main_token[0] == ";"):
                lex()
                genQuad(":=", e, "_", id_temp)
            else:
                sys.exit("SyntaxError: ';' was expected"+" , line "+str(line))
        elif main_token[0] == "int":
            lex()
            if(main_token[0] == "("):
                lex()
                if(main_token[0] == "input"):
                    lex()
                    genQuad("in", id_temp, "_", "_")
                    if(main_token[0] == "("):
                        lex()
                        if(main_token[0] == ")"):
                            lex()
                            if(main_token[0] == ")"):
                                lex()
                                if(main_token[0] == ";"):
                                    lex()
                                else:
                                    sys.exit("SyntaxError: ';' was expected"+" , line "+str(line))
                            else:
                                sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
                        else:
                             sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
                    else:
                        sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))
                else:
                    sys.exit("SyntaxError: Keyword 'input' was expected"+" , line "+str(line))
            else:
                sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: Missing assignement"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: Probably '=' was expected or missing keyword 'def'(less possible)"+" , line "+str(line))

def print_stat():
    global main_token
    if(main_token[0] == "("):
        lex()
        e = expression()
        if(main_token[0] == ")"):
            lex()
            if(main_token[0] == ";"):
                lex()
                genQuad("out", e, "_", "_")
            else:
                sys.exit("SyntaxError: ';' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))

def return_stat():
    global main_token
    Symbol_Table.current_scope.Return_check += 1
    if(main_token[0] == "("):
        lex()
        e = expression()
        if(main_token[0] == ")"):
            lex()
            if(main_token[0] == ";"):
                lex()
                genQuad("ret", e, "_", "_")
            else:
                sys.exit("SyntaxError: ';' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: the ')' was expected"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))

def if_stat():
    global main_token
    if main_token[0] == "(":
        lex()
        c = [0,0]
        c = condition()
        if main_token[0] == ")":
            lex()
            if main_token[0] == ":":
                lex()
                backpatch(c[1], nextQuad())
                if((main_token[1] == "id") or (main_token[0] == "print") or (main_token[0] == "return") or (main_token[0] == "if") or (main_token[0] == "while")):
                    statement()
                    ifList = [0,0]
                    ifList = makeList(nextQuad())
                    genQuad("jump", '_', '_', '_')
                    backpatch(c[0], nextQuad())
                    if(main_token[0] == "else"):
                        lex()
                        if(main_token[0] == ":"):
                            lex()
                            if((main_token[1] == "id") or (main_token[0] == "print") or (main_token[0] == "return") or (main_token[0] == "if") or (main_token[0] == "while")):
                                statement()
                            elif(main_token[0] == "#{"):
                                lex()
                                statements()
                                if(main_token[0] == "#}"):
                                    lex()
                                else:
                                    sys.exit("SyntaxError: '#}' was expected"+" , line "+str(line))
                            else:
                                sys.exit("SyntaxError: Missing statements in if-block"+" , line "+str(line))
                        else:
                            sys.exit("SyntaxError: ':' was expected"+" , line "+str(line))
                    backpatch(ifList, nextQuad())
                elif(main_token[0] == "#{"):
                    lex()
                    statements()
                    ifList = [0,0]
                    ifList = makeList(nextQuad())
                    genQuad("jump", '_', '_', '_')
                    backpatch(c[0], nextQuad())
                    if(main_token[0] == "#}"):
                        lex()
                        if(main_token[0] == "else"):
                            lex()
                            if(main_token[0] == ":"):
                                lex()
                                if((main_token[1] == "id") or (main_token[0] == "print") or (main_token[0] == "return") or (main_token[0] == "if") or (main_token[0] == "while")):
                                    statement()
                                elif(main_token[0] == "#{"):
                                    lex()
                                    statements()
                                    if(main_token[0] == "#}"):
                                        lex()
                                    else:
                                        sys.exit("SyntaxError: '#}' was expected"+" , line "+str(line))
                                else:
                                    sys.exit("SyntaxError: Missing statements in if-block: "+" , line "+str(line))
                            else:
                                sys.exit("SyntaxError: ':' was expected"+" , line "+str(line))
                    else:
                        sys.exit("SyntaxError: '#}' was expected"+" , line "+str(line))
                    backpatch(ifList, nextQuad())
                else:
                    sys.exit("SyntaxError: Missing statements in if-block"+" , line "+str(line))
            else:
                sys.exit("SyntaxError: ':' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))

def while_stat():
    global main_token
    Bquad = nextQuad()
    B = [0,0]
    if(main_token[0] == "("):
        lex()
        B = condition()
        if(main_token[0] == ")"):
            lex()
            if(main_token[0] == ":"):
                lex()
                backpatch(B[1], nextQuad())
                if((main_token[1] == "id") or (main_token[0] == "print") or (main_token[0] == "return") or (main_token[0] == "if") or (main_token[0] == "while")):
                    statement()
                    genQuad("jump", "_", "_", Bquad)
                    backpatch(B[0], nextQuad())
                elif(main_token[0] == "#{"):
                    lex()
                    statements()
                    genQuad("jump", "_", "_", Bquad)
                    backpatch(B[0], nextQuad())
                    if(main_token[0] == "#}"):
                        lex()
                    else:
                        sys.exit("SyntaxError: '#}' was expected"+" , line "+str(line))
                else:
                    sys.exit("SyntaxError: Missing statements in while-block"+" , line "+str(line))
            else:
                sys.exit("SyntaxError: ':' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))

def id_list(parent_call):
    global main_token
    
    if(main_token[1] == "id"):
        if(Symbol_Table.lookup_for_dublicate_error(main_token[0], parent_call) == 1):
            sys.exit("Error: Dublicate variable name"+" , line "+str(line))
        entity = Variable(main_token[0], "-")
        Symbol_Table.add_in_entity_list(entity)
        if(parent_call == "function"):
            if (Symbol_Table.look_up_FormalParameter(main_token[0]) == 1):
                sys.exit("Error: Dublicate formal parameter name"+" , line "+str(line))
            entity1 = FormalParameter(main_token[0], "CV")
            Symbol_Table.current_scope.Entity_list.pop(-1)
            Symbol_Table.current_scope.frameLength -= 4
            entity2 = Parameter(main_token[0], entity1.mode, entity.offset)
            Symbol_Table.add_in_entity_list(entity2)
            Symbol_Table.add_formal_parameter(entity1)
        lex()
        while(main_token[0] == ","):
            lex()
            if(main_token[1] == "id"):
                if(Symbol_Table.lookup_for_dublicate_error(main_token[0], parent_call) == 1):
                    sys.exit("Error: Dublicate variable name"+" , line "+str(line))
                entity = Variable(main_token[0], "-")
                Symbol_Table.add_in_entity_list(entity)
                if(parent_call == "function"):
                    if (Symbol_Table.look_up_FormalParameter(main_token[0]) == 1):
                        sys.exit("Error: Dublicate formal parameter name"+" , line "+str(line))
                    entity1 = FormalParameter(main_token[0],"CV")
                    Symbol_Table.current_scope.Entity_list.pop(-1)
                    Symbol_Table.current_scope.frameLength -= 4
                    entity2 = Parameter(main_token[0], entity1.mode, entity.offset)
                    Symbol_Table.add_in_entity_list(entity2)
                    Symbol_Table.add_formal_parameter(entity1)
                lex()
                continue
            else:
                sys.exit("SyntaxError: There is probably an illegal variable name: "+main_token[0]+" , line "+str(line))

def expression():
    global main_token
    t1_1 = optional_sign()
    t1 = term()
    if t1_1 == '-':
        w = newTemp()
        entity = TempVariable(w, "-")
        Symbol_Table.add_in_entity_list(entity)
        genQuad("-", 0, t1, w)
        t1 = w
    while (main_token[1] == "addOperator"):
        tempAddOperator = main_token[0]
        lex()
        t2 = term()
        w = newTemp()
        entity = TempVariable(w, "-")
        Symbol_Table.add_in_entity_list(entity)
        genQuad(tempAddOperator, t1, t2, w)
        t1 = w
    return t1        
    
def term():
    global main_token
    f1 = factor()
    while(main_token[1] == "mulOperator"):
        tempMulOperator = main_token[0]
        lex()
        f2 = factor()
        w  = newTemp()
        entity = TempVariable(w, "-")
        Symbol_Table.add_in_entity_list(entity)
        genQuad(tempMulOperator, f1, f2, w)
        f1 = w
    return f1

def factor():
    global main_token
    if(main_token[1] == "number"):
        f = main_token[0]
        lex()
        return f
    elif(main_token[0] == "("):
        lex()
        f = expression()
        if(main_token[0] == ")"):
            lex()
        else:
            sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
        return f
    elif(main_token[1] == "id"):
        f = main_token[0]
        lex()
        e = idtail(f)
        if e == '':
            return f
        else:
            return e
    else:
        sys.exit("SyntaxError: Missing term"+" , line "+str(line))

def idtail(callFunction):
    global main_token
    e = ''
    if(main_token[0] == "("):
        lex()
        actual_par_list()
        temp = newTemp()
        entity = TempVariable(temp, "-")
        Symbol_Table.add_in_entity_list(entity)
        genQuad("par", temp, "ret", '_')
        genQuad("call", callFunction, '_', '_')
        e = temp 
        if(main_token[0] == ")"):
            lex()
        else:
            sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
    return e

def actual_par_list():
    global main_token
    if main_token[0] == ')':
        return
    e = expression()
    genList=[]
    genList.append(e)
    while(main_token[0] == ","):
        lex()
        e1 = expression()
        genList.append(e1)
    for quad in genList: 
        genQuad("par", quad, "cv", '_')
    
def optional_sign():
    global main_token
    f = ''
    if(main_token[1] == "addOperator"):
        f = main_token[0]
        lex()
        return  f
    
def condition():
    global main_token
    q1 = [0,0]
    q1 = bool_term()
    b = [0,0]
    b[1] = q1[1]  #b.true = q1.true
    b[0] = q1[0]  #b.false = q1.false
    while(main_token[0] == "or"):
        backpatch(b[0], nextQuad())
        lex() 
        q2 = [0,0]
        q2 = bool_factor()
        b[1] = mergeList(b[1], q2[1])
        b[0] = q2[0]
    return b

def bool_term():
    global main_token
    r1 = [0,0]
    q = [0,0]
    r1 = bool_factor()
    q[1] = r1[1]
    q[0] = r1[0] 
    while(main_token[0] == "and"):
        backpatch(q[1], nextQuad())
        lex()
        r2 = [0,0]
        r2 = bool_factor()
        q[0] = mergeList(q[0], r2[0])
        q[1] = r2[1]
    return q

def bool_factor():
    global main_token
    r = [0,0]
    if (main_token[0] == "not"):
        lex()
        if (main_token[0] == "["):
            lex()
            b = [0,0]
            b = condition()
            if(main_token[0] == "]"):
                lex()
            else:
              sys.exit("SyntaxError: ']' was expected"+" , line "+str(line))
        else:
             sys.exit("SyntaxError: '[' was expected"+" , line "+str(line))
        r[1] = b[0]
        r[0] = b[1]
        return r 
    elif main_token[0] == "[":
        lex()
        b = [0,0]
        b = condition()
        if(main_token[0] == "]"):
            lex()
        else:
            sys.exit("SyntaxError: ']' was expected"+" , line "+str(line))
        r[1] = b[1]
        r[0] = b[0]
        return r
    elif ((main_token[1] == "addOperator") or (main_token[1] == "number") or (main_token[0] == "(")   or (main_token[1] == "id")):
        e1 = expression()
        if(main_token[1] == "relOperator"):
            tempRelOperator = main_token[0]
            lex()
            e2 = expression()
        else:
            sys.exit("SyntaxError: A RelOperator was expected"+" , line "+str(line))
        r[1] = makeList(nextQuad())
        genQuad(tempRelOperator, e1, e2, '_')
        r[0] = makeList(nextQuad())
        genQuad("jump", '_', '_', '_')
        return r
    else:
        sys.exit("SyntaxError: There is an empty bool factor"+" , line "+str(line))

def call_main_part():
    global main_token
    global flag2
    if(main_token[0] == "if"):
        lex()
        if(main_token[0] == "__name__"):
            lex()
            if(main_token[0] == "=="):
                lex()
                if(main_token[0] == '"__main__"'):
                    lex()
                    if(main_token[0] == ":"):
                        lex()
                        genQuad("begin_block", "main", "_", "_")
                        Symbol_Table.add_scope()
                        for entity in main_functions_table:
                            Symbol_Table.add_in_entity_list(entity)
                        while(main_token[1] == "id"):
                            if (main_token[0] not in main_functions_names):
                                sys.exit("Error: Invalid main function name"+" , line "+str(line))
                            flag2 = 0
                            tempId = main_token[0]
                            lex()
                            genQuad("call", tempId, "_", "_")
                            main_function_call()
                        genQuad("halt", "_", "_", "_")
                        genQuad("end_block", "main", "_", "_")
                        produce_finalCode()
                        if flag2:
                            sys.exit("SyntaxError: Missing Main fuction calls"+" , line "+str(line))
                    else:
                        sys.exit("SyntaxError: ':' was expected'"+" , line "+str(line))
                else:
                    sys.exit("SyntaxError: '__main__' was expected'"+" , line "+str(line))
            else:
                sys.exit("SyntaxError: '==' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: '__name__' was expected'"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: 'if' was expected for __main__ or keyword 'def is missing' "+" , line "+str(line))

def main_function_call():
    global main_token
    if(main_token[0] == "("):
        lex()
        if(main_token[0] == ")"):
            lex()
            if(main_token[0] == ";"):
                lex()
            else:
                sys.exit("SyntaxError: ';' was expected"+" , line "+str(line))
        else:
            sys.exit("SyntaxError: ')' was expected"+" , line "+str(line))
    else:
        sys.exit("SyntaxError: '(' was expected"+" , line "+str(line))


def main(namefile):    
    global file
    global main_token
    global lex_table
    file = open(namefile, "rb")
    lex()
    syntax_analyzer()
    
    #for element in lex_table:
        #print(element)
    

if __name__ == "__main__":
    main(sys.argv[1])
