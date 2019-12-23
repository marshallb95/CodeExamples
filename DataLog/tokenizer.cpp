#include "tokenizer.h"
Token::Token(string label, string token, int lineNum) {
    lineNumber = lineNum;
    strToken = token;
    tokenLabel = label;
}

void Token::printToken() {
    cout << "(" << tokenLabel << ",\"" << strToken << "\"," << lineNumber << ")" << endl;
    return;
}

string Token::getLabel() const {
	return tokenLabel;
}

int Token::getLineNumber() const {
	return lineNumber;
}

string Token::getStrToken() const {
	return strToken;
}
