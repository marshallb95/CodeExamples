#include "parser.h"
#include "Predicate.h"
#include "Parameter.h"
#include "DataLogProgram.h"
#include "Rule.h"

int GLOB_IND = 0;

Parser::Parser(vector<Token> scannedTokens) {
	compTokens = scannedTokens;

	return;
}

DataLogProgram Parser::getDataLog() {
	return myDataLog;
}

void Parser::match(string curToken) {
	if(curToken == compTokens[GLOB_IND].getLabel()) {
		//cout << "GlOB_IND: " << GLOB_IND << endl;
		//cout << "Expected match is: " << curToken << endl;
		//cout << "Actual Token is: " << compTokens[GLOB_IND].getLabel() << endl;
		//cout << endl;
		GLOB_IND += 1;
		return;
	}
	else {
		//cout << "Index at failure is: " << GLOB_IND;
		//cout << "Expected match was: " << curToken << endl;
		//cout << "Actual Token is: " << compTokens[GLOB_IND].getLabel() << endl;
		//cout << endl;
		throw compTokens[GLOB_IND];
		return;
	}
}
void Parser::parse() {
	//cout << "In parse" << endl;
	try {
		datalogProgram();
	}
	catch (Token error) {
//		cout << "Failure!" << endl;
//		cout << "  ";
		error.printToken();
		return;
	}
//	cout << "Success!" << endl;
//	myDataLog.printDataLog();	
	return;
}

void Parser::datalogProgram() {
	//cout << "In datalog" << endl;
	match("SCHEMES");
	match("COLON");
	scheme();
	schemeList();
	match("FACTS");
	match("COLON");
	factList();
	match("RULES");
	match("COLON");
	ruleList();
	match("QUERIES");
	match("COLON");
	query();
	queryList();
	match("EOF");
	return;
}

void Parser::schemeList() {
	//cout << "InSchemeList" << endl;
	if(compTokens[GLOB_IND].getLabel() == "FACTS") {
		return;
	}
	else {
		scheme();
		schemeList();
	}
	return; 
}

void Parser::factList() {
	//cout << "In factlist" << endl;
	if(compTokens[GLOB_IND].getLabel() == "RULES") {
		return;
	}
	else {
		fact();
		factList();
	}
	return;
}
void Parser::ruleList() {
	//cout << "In ruleList" << endl;
	if(compTokens[GLOB_IND].getLabel() == "QUERIES") {
		return;
	}
	else {
		rule();
		ruleList();
	}
	return; 
}

void Parser::queryList() {
	//cout << "In queryList" << endl;
	if(compTokens[GLOB_IND].getLabel() == "EOF") {
		return;
	}
	else {
		query();
		queryList();
	}
	return; 
}

void Parser::scheme() {
	//cout << "In scheme" << endl;
	match("ID");
	Predicate pred;
	pred.addName(compTokens[GLOB_IND-1].getStrToken());
	//cout << "After predicate name made" << endl;
	//cout << pred.predToString() << endl;
	match("LEFT_PAREN");
	match("ID");
	Parameter myParam;
	bool constant = false;
	myParam.setIsConstant(constant);
	//myParam.addToParameter("(");
	myParam.addToParameter(compTokens[GLOB_IND-1].getStrToken());
	pred.addParameter(myParam);
	//cout << "After first parameter added in scheme" << endl;
	//cout << pred.predToString() << endl;
	idList(pred);
	match("RIGHT_PAREN");
	myDataLog.addScheme(pred);
	return;
}

void Parser::fact() {
	//cout << "In fact" << endl;
	match("ID");
	Predicate factPred;
	factPred.addName(compTokens[GLOB_IND-1].getStrToken());
	match("LEFT_PAREN");
	match("STRING");
	Parameter factParam;
	bool constant = false;
	factParam.setIsConstant(constant);
	//factParam.addToParameter("(");
	factParam.addToParameter(compTokens[GLOB_IND-1].getStrToken());
	myDataLog.addToDomain(compTokens[GLOB_IND-1].getStrToken());
	factPred.addParameter(factParam);
	stringList(factPred);
	match("RIGHT_PAREN");
	//factParam.addToParameter(")");
	match("PERIOD");
	//factParam.addToParameter(".");
	myDataLog.addFact(factPred);
	return; 
}
void Parser::rule() {
	//cout << "In rule" << endl;
	Rule myRule;
	headPredicate(myRule);
	match("COLON_DASH");
	Predicate rulepred = predicate();
	myRule.addToBody(rulepred);
	predicateList(myRule);
	match("PERIOD");
	myDataLog.addRule(myRule);
	return; 
}

void Parser::query() {
	//cout << "In query" << endl;
	Predicate myQuery = predicate();
	match("Q_MARK");
	myDataLog.addQuery(myQuery);
	return;
}

void Parser::headPredicate(Rule &myRule) {
	//cout << "In head Predicate" << endl;
	Predicate headPred;
	match("ID");
	headPred.addName(compTokens[GLOB_IND-1].getStrToken());
	match("LEFT_PAREN");
	match("ID");
	Parameter param;
	bool constant = false;
	param.setIsConstant(constant);
	param.addToParameter(compTokens[GLOB_IND-1].getStrToken());
	headPred.addParameter(param);
	idList(headPred);
	match("RIGHT_PAREN");
    myRule.addHead(headPred);
	return; 
}

Predicate Parser::predicate() {
	//cout << "In predicate" << endl;
	Predicate pred;
	match("ID");
	pred.addName(compTokens[GLOB_IND-1].getStrToken());
	match("LEFT_PAREN");
	Parameter param;
	//param.addToParameter("(");
	parameter(param);
	pred.addParameter(param);
	parameterList(pred);
	match("RIGHT_PAREN");
	return pred;
}

void Parser::predicateList(Rule &myRule) {
	//cout << "In predicateList" << endl;
	if(compTokens[GLOB_IND].getLabel() == "PERIOD") {
		return;
	}
	else {
		match("COMMA");
		Predicate myPred = predicate();
		myRule.addToBody(myPred);
		predicateList(myRule);
	}
	return;
}

void Parser::parameterList(Predicate &pred) {
	//cout << "In parameterList" << endl;
	if(compTokens[GLOB_IND].getLabel() == "RIGHT_PAREN") {
		return;
	}
	else {
		match("COMMA");
		Parameter param;
		parameter(param);
		pred.addParameter(param);
		parameterList(pred);
	}
	return;
}

void Parser::stringList(Predicate &factPred) {
	//cout << "In stringList" << endl;
	if(compTokens[GLOB_IND].getLabel() == "RIGHT_PAREN") {
		return;
	}
	else {
		match("COMMA");
		match("STRING");
		//factParam.addToParameter(",");
		Parameter factParam;
		bool constant = true;
		factParam.setIsConstant(constant);
		factParam.addToParameter(compTokens[GLOB_IND-1].getStrToken());
	//	cout << "String being added to parameter is: " << compTokens[GLOB_IND-1].getStrToken();
	//	cout << "Parameter currently is " << factParam.paramToString();
		myDataLog.addToDomain(compTokens[GLOB_IND-1].getStrToken());
		factPred.addParameter(factParam);
		stringList(factPred);
		return;
	}
}

void Parser::idList(Predicate &myPred) {
	//cout << "In idList" << endl;
	if(compTokens[GLOB_IND].getLabel() == "RIGHT_PAREN") {
		//Parameter parameter;
		//parameter.addToParameter(")");
		//myPred.addParameter(parameter);
		return;
	}
	else {
		match("COMMA");
		match("ID");
		Parameter parameter;
		bool constant = false;
		parameter.setIsConstant(constant);
		//parameter.addToParameter(",");
		parameter.addToParameter(compTokens[GLOB_IND-1].getStrToken());
		myPred.addParameter(parameter);
		idList(myPred);
		return;
	}
}

void Parser::parameter(Parameter &param) {
	//cout << "In parameter" << endl;
	if(compTokens[GLOB_IND].getLabel() == "STRING") {
		match("STRING");
		bool constant = true;
		param.setIsConstant(constant);
		param.addToParameter(compTokens[GLOB_IND-1].getStrToken());
		return;
	}
	else if(compTokens[GLOB_IND].getLabel() == "ID") {
		match("ID");
		bool constant = false;
		param.setIsConstant(constant);
		param.addToParameter(compTokens[GLOB_IND-1].getStrToken());
		return;
	}
	else {
		expression(param);
	}
	return;
}

void Parser::expression(Parameter &param) {
	//UNSURE WHETHER TO EVALUATE AS A CONSTANT OR A VARIABLE
	//cout << "In expression" << endl;
	match("LEFT_PAREN");
	param.addToParameter("(");
	parameter(param);
	parse_operator(param);
	parameter(param);
	match("RIGHT_PAREN");
	param.addToParameter(")");
	return;
}

void Parser::parse_operator(Parameter &param) {
	//cout << "In operator" << endl;
	if(compTokens[GLOB_IND].getLabel() == "ADD") {
		match("ADD");
		param.addToParameter("+");
		return;
	}
	else {
		match("MULTIPLY");
		param.addToParameter("*");
		return;
	}
	return;
}

