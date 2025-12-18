import sys

def defineType(file, baseName, className, fields):
    file.write("\tclass " + className + "(\r\n")

    for field in fields.split(", "):
        t, name = field.split(' ')
        file.write("\t\tval " + name + " : " + t + ",\r\n")

    file.write("\t) : " + baseName + "(){\r\n")

    file.write("\r\n\t\toverride fun <R> accept(visitor: Visitor<R>): R {\r\n")
    file.write("\t\t\treturn visitor.visit" + className + baseName + "(this);\r\n}\r\n")

    file.write("\t\t}\r\n\r\n")

def defineVisitor(file, baseName, types):
    file.write("\tinterface Visitor<out R> {\r\n")
    
    for t in types:
        typeName = t.split(':')[0].strip()
        file.write("\t\tfun visit" + typeName + baseName + '(' + baseName.lower() + " : " + typeName + ") : R;\r\n")

    file.write("}\r\n\r\n")

def defineAst(outputDir, baseName, types):
    path = outputDir + "/" + baseName + ".kt"
    with open(path, 'w') as f:
        f.write("import kotlin.collections.List\r\n")
        f.write("\r\n")
        f.write("abstract class " + baseName + " {\r\n")

        defineVisitor(f, baseName, types)

        f.write("\r\n\t// generic function for double dispatch\r\n\tabstract fun <R> accept(visitor: Visitor<R>): R\r\n\r\n")

        for x in types:
            className, fields = x.split(':')
            defineType(f, baseName, className.strip(), fields.strip())



        f.write("}\r\n")

if len(sys.argv) < 2:
    print("GenerateAst.py output-dir")
    sys.exit(0)

outputDir = sys.argv[1]
defineAst(outputDir, "Expr", [
	"Binary		: Expr left, Token operator, Expr right",
    "Assign     : Token name, Expr value",
	"Grouping	: Expr expression",
	"Literal	: Any? value",
	"Unary		: Token operator, Expr right",
    "Variable    : Token name"
])
defineAst(outputDir, "Stmt", [
    "Expression	: Expr expression",
    "Print		: Expr expression",
    "Var        : Token name, Expr? initializer"
])
