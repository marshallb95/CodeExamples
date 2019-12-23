#include "scanner.h"

set<char> singleC = {'.', ',', '?', '(', ')', ':', '*', '+'};

Scanner::Scanner(string file_name) {
    filename = file_name;
    lineNumber = 1;
    altLineNum = 1;
}
void Scanner::scanInfile() {
    ifstream inFile(filename);
    stringstream ss;
    char carac;
    string str;
    if(!inFile.is_open()) {
        cout << "File failed to open properly" << endl;
    }
    while(inFile.get(carac)) {
        //If newline character encountered, increment line number
        if(carac == '\n') {
            lineNumber += 1;
        }
        //if :- encountered
        if(carac == ':' && inFile.peek() == '-') {
            ss << carac;
            inFile.get(carac);
            ss << carac;
            str = ss.str();
            ss.str("");
            scTokens.push_back(str);
            tokenMap.push_back(lineNumber);
        }
        //If one of the single tokens encountered
        else if(singleC.find(carac) != singleC.end()) {
            ss << carac;
            str = ss.str();
            ss.str("");
            scTokens.push_back(str);
            tokenMap.push_back(lineNumber);
        }
        //If a comment encountered
        else if(carac == '#') {
            ss << carac;
            //If multiline block comment
            if(inFile.peek() == '|') {
                altLineNum = lineNumber;
                char nextChar;
                inFile.get(nextChar);
                //cout << "Next char is" << nextChar << endl;
                ss << nextChar;
                //goes till either end of file or #| encountered
                //cout << carac << " " << inFile.peek() << endl;
                
                while(carac != '|' || inFile.peek() != '#') {
                    //cout <<"entered while loop" << endl;
                    inFile.get(carac);
                    //cout << "carac in while loop is" << carac << endl;
                    //cout << "inFile peek is " << inFile.peek() << endl;
                    //cout << (inFile.peek() != '#') << endl;
                    if(carac == '\n') {
                        lineNumber += 1;
                    }
                    ss << carac;
                    if(inFile.peek() == EOF) {
                        //cout << "Peek hit the end of the file" << endl;
                        //unsure if need to add new line here to account for EOF
                        break;
                    }
                }
                //cout << "left while loop" << endl;
                if(inFile.peek() != EOF) {
                    inFile.get(carac);
                    ss << carac;
                }
                str = ss.str();
                ss.str("");
                scTokens.push_back(str);
                tokenMap.push_back(altLineNum);
            }
            //If single line comment
            else {
                while(inFile.peek() != EOF && inFile.peek() != '\n') {
                    //cout << "Entered else while loop" << endl;
                    carac = inFile.get();
                    //cout << "Infile.peek is " << inFile.peek() << endl;
                    //cout << "carac is " << carac << endl;
                    ss << carac;
                }
                str = ss.str();
                ss.str("");
                scTokens.push_back(str);
                tokenMap.push_back(lineNumber);
            }
        }
        //If a string
        else if (carac == '\'') {
            //cout << "entered in string" << endl;
            ss << carac;
            altLineNum = lineNumber;
            while(inFile.peek() != EOF) {
                inFile.get(carac);
                if(carac == '\n') {
                    lineNumber += 1;
                }
                ss << carac;
                //cout << "carac in string is " << carac << endl;
                if(carac == '\'' && inFile.peek() == '\'') {
                    //cout << "In escape sequence for string" << endl;
                    //cout << "Carac is " << carac << endl;
                    inFile.get(carac);
                    //cout << "Following carac in escape sequence is" << carac << endl;
                    ss << carac;
                }
                else if (carac == '\'' && inFile.peek() != '\'') {
                    //cout << "End of string encountered" << endl;
                    break;
                }
                else {
                    continue;
                }
            }
        str = ss.str();
        ss.str("");
        scTokens.push_back(str);
        tokenMap.push_back(altLineNum);
        }
        //Dealing with ID's, including Facts, Rules, and Queries
        else if(isalpha(carac)) {
            ss << carac;
            while(isalnum(inFile.peek())){
                inFile.get(carac);
                ss << carac;
            }
            str = ss.str();
            ss.str("");
            scTokens.push_back(str);
            tokenMap.push_back(lineNumber);
        }
        //If leads with digit or undefined character
        else if(isdigit(carac) || isspace(carac) == false) {
            ss << carac;
            str = ss.str();
            ss.str("");
            scTokens.push_back(str);
            tokenMap.push_back(lineNumber);
        }
        else if (carac == EOF) {
            scTokens.push_back("_EOF");
            tokenMap.push_back(lineNumber);
        }
        else {
                //cout << "Char is " << carac << endl;
        }
    }
    
    inFile.close();
    scTokens.push_back("_EOF");
    tokenMap.push_back(lineNumber);
    //for(unsigned int i = 0; i < scTokens.size(); i++) {
        //cout  << "i is " << to_string(i) << " " << scTokens[i] << endl;
        //cout << tokenMap[scTokens[i]] << endl;
    //}
    return; //TODO
}

//counts number of characters in a string
int Scanner::count(string str, char ch) {
    int n = str.size();
    int count = 0;
    for(int i = 0; i < n; i++) {
        if(str[i] == ch) {
            count += 1;
        }
    }
    return count;
}

string Scanner::tokenLabeler(string sToken) {
    if(sToken.size() == 1 && !isalpha(sToken[0])) {
        if(sToken == "(") {
            return "LEFT_PAREN";
        }
        else if (sToken == ")") {
            return "RIGHT_PAREN";
        }
        else if (sToken == ":") {
            return "COLON";
        }
        else if (sToken == ",") {
            return "COMMA";
        }
        else if (sToken == ".") {
            return "PERIOD";
        }
        else if (sToken == "?") {
            return "Q_MARK";
        }
        else if(sToken == "*") {
            return "MULTIPLY";
        }
        else if (sToken == "+") {
            return "ADD";
        }
        else {
            return "UNDEFINED";
        }
    }
    else if (sToken == ":-") {
        return "COLON_DASH";
    }
    else if(sToken == "Rules") {
        return "RULES";
    }
    else if (sToken == "Schemes") {
        return "SCHEMES";
    }
    else if (sToken == "Facts") {
        return "FACTS";
    }
    else if (sToken == "Queries") {
        return "QUERIES";
    }
    else if(sToken[0] == '\'' && sToken[sToken.size() -1] == '\'') {
        return "STRING";
    }
    else if (isalpha(sToken[0])) {
        return "ID";
    }
    else if(sToken[0] == '#' && sToken[1] != '|') {
        return "COMMENT";
    }
    else if (sToken.find("#|") != string::npos && sToken.find("|#") != string::npos) {
        return "COMMENT";
    }
    else {
        return "UNDEFINED";
        
    }
}

void Scanner::makeTokens() {
    int lineNum = 0;
    string label = "empty";
    for(unsigned int i = 0; i < scTokens.size()-1; i++) {
            lineNum = tokenMap[i];
            label = tokenLabeler(scTokens[i]);
	    //If you're running into problems with the LAB2, they may be where part of the problem is
	    if(label == "COMMENT"){
		    continue;
	    }
            //cout << "Token is: " << scTokens[i] << " Label is:" << label << " LineNum is: " << lineNum << endl;
            Token tok = Token(label, scTokens[i], lineNum);
            completeTokens.push_back(tok);
    }
    //possibly where the problem was coming from
    lineNum = tokenMap[scTokens.size()-1];
    Token tok = Token("EOF", "", lineNum);
    completeTokens.push_back(tok);
    return;
}

void Scanner::printTokens() {
    for(unsigned int i = 0; i < completeTokens.size(); i++) {
	//cout << "Index is: " << i << endl;
        completeTokens[i].printToken();
	//cout << endl;
    }
    cout << "Total Tokens = " << completeTokens.size();
    return;
}

vector<Token> Scanner::getCompleteTokens() const{
	return completeTokens;
}
