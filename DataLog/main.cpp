#include "scanner.h"
#include "tokenizer.h"
#include "parser.h"
#include "Interpreter.h"

using namespace std;


int main(int argc, char* argv[]) {
    string filename = argv[1];
    Scanner myScanner(filename);
    myScanner.scanInfile();
    myScanner.makeTokens();
    //myScanner.printTokens();
    Parser myParser(myScanner.getCompleteTokens());
    myParser.parse();
    Interpreter myInterp(myParser.getDataLog());
    myInterp.makeDataBase();
    myInterp.buildDependencyGraph();
    myInterp.buildReverseDependencyGraph();
    vector<SCC> comp = myInterp.findSCC();
    myInterp.evalSCC(comp);
    //myInterp.evaluateRules();
    myInterp.evaluateAllQueries();
    return 0;
}
