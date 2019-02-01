# markov_chains.py
"""Volume II: Markov Chains
Brandon Marshall
Section 2
November 1, 2018
"""

import numpy as np
from scipy import linalg as la

# Problem 1
def random_chain(n):
    """Create and return a transition matrix for a random Markov chain with
    'n' states. This should be stored as an nxn NumPy array.
    """
    #1x1 matrix normalized is just 1
    if n == 1:
        return np.array([1])
    else:
        tran = np.random.rand(n,n)
        temp = tran.sum(axis = 0)
        #normalize each column
        temp = temp.reshape(1,len(tran))
        return tran / temp
       
    raise NotImplementedError("Problem 1 Incomplete")


# Problem 2
def forecast(n):
    """Forecast n days of weather given that today is hot.
        Parameters:
            n(int): # of days to forecast
    """
    if n <= 0:
        raise ValueError("Cannot forecast for today(0) or for past days(-#'s)")
    if type(n) is not int:
        raise TypeError("n must be an int")
    transition = np.array([[0.7, 0.6], [0.3, 0.4]])
    forecast_list = []
    #Sample for the first day
    forecast_list.append(np.random.binomial(1, transition[1, 0]))
    if n == 1:
        return forecast_list
    else:
        for i in range(0,n-1):
            #calculate the probability of whether or not it will be cold based off of what the last predicition was
            forecast_list.append(np.random.binomial(1, transition[1, 1*forecast_list[len(forecast_list)-1]]))
        return forecast_list


# Problem 3
def four_state_forecast(days):
    """Run a simulation for the weather over the specified number of days,
    with mild as the starting state, using the four-state Markov chain.
    Return a list containing the day-by-day results, not including the
    starting day.

    Examples:
        >>> four_state_forecast(3)
        [0, 1, 3]
        >>> four_state_forecast(5)
        [2, 1, 2, 1, 1]
    """
    if days <= 0:
        raise ValueError("Can only forecast for positive integers")
    if type(days) is not int:
        raise TypeError("days must be an int")
    #assign value to each state
    state = np.array([0,1,2,3])
    #probabilities for tomorrow
    transition = np.array([[0.5, 0.3, 0.1, 0],[0.3, 0.3, 0.3, 0.3],[0.2, 0.3, 0.4, 0.5], [0,0.1,0.2,0.2]])
    forecast_list = []
    #calculate state of tomorrow based off today being mild, since we only want to add a number to the list, we multiply by the right weight and add the row, which gives us the number we want
    forecast_list.append(sum(np.random.multinomial(1,transition[:,1]) * state))
    for i in range(0,days-1):
        forecast_list.append(sum(np.random.multinomial(1,transition[:,forecast_list[len(forecast_list)-1]])*state))
    return forecast_list
    raise NotImplementedError("Problem 3 Incomplete")


# Problem 4
def steady_state(A, tol=1e-12, N=40):
    """Compute the steady state of the transition matrix A.

    Inputs:
        A ((n,n) ndarray): A column-stochastic transition matrix.
        tol (float): The convergence tolerance.
        N (int): The maximum number of iterations to compute.

    Raises:
        ValueError: if the iteration does not converge within N steps.

    Returns:
        x ((n,) ndarray): The steady state distribution vector of A.
    """
    #m=n, m is used as place holder so we can obtain n
    m,n = A.shape
    k = 1
    xlist = []
    x0 = np.random.rand(n,1)
    #normalize x0
    x0 = x0/x0.sum(axis=0)
    xlist.append(x0)
    x = np.dot(A, xlist[len(xlist)-1])
    xlist.append(x)
    #produce xk until either k > N or ||xk-1 - xk|| < tol
    while la.norm(xlist[len(xlist)-2] - xlist[len(xlist)-1]) >= tol:
        k +=1
        if k > N:    
            raise ValueError("A^k does not converge")
        x = np.dot(A,xlist[len(xlist)-1])
        xlist.append(x)
    return xlist[len(xlist)-1]
    raise NotImplementedError("Problem 4 Incomplete")


# Problems 5 and 6
class SentenceGenerator(object):
    """Markov chain creator for simulating bad English.

    Attributes:
        transition (ndarray (N+2)x(N+2) where N is the number of unique words in the training set): the transition matrix for the training set
        indexToWord (dictionary): A dictionary that maps the index for each unique word to that word
        wordToindex (dictionary): A dictionary that maps each unique word to its index
        training_set(list): the training set read in from the file
        unique_words(set): A set of the unique words in the training set
        states(list): a list of the states of the transition matrix
        indices(ndarray (N+2,)): an array that contains the indices in sequential order (used for mapping with the multinomial distribution)
    Example:
        >>> yoda = SentenceGenerator("Yoda.txt")
        >>> print(yoda.babble())
        The dark side of loss is a path as one with you.
    """
    def __init__(self, filename):
        """Read the specified file and build a transition matrix from its
        contents. You may assume that the file has one complete sentence
        written on each line.
        """
        #initialize unique word set
        self.unique_words = set()
        #read in training set
        with open(filename, 'r') as myfile:
            self.training_set = myfile.readlines()
        #go through training set and build set of unique words
        for i in self.training_set:
            i = i.strip().split()
            for j in i:
                self.unique_words.add(j)
        #build matrix with size of unique set+2, which accounts for $tart and $top
        self.transition = np.zeros((len(self.unique_words) +2, len(self.unique_words)+2))
        #initialize list of states
        self.states = []
        self.states.append("$tart")

        #index_dict contains the correlating index value for the word, thus in the transition matrix, if $tart had index 0, then 0,0 would correlate to $tart transitioning to itself
        self.wordToindex = {}
        self.indexToWord = {}
        self.wordToindex.update({self.states[len(self.states)-1] : len(self.states) -1})
        self.indexToWord.update({len(self.states)-1 : self.states[len(self.states)-1]})
        self.wordToindex.update({"$top" : len(self.unique_words) + 1})
        self.indexToWord.update({len(self.unique_words)+1 : "$top"})

        for sentence in self.training_set:
            #split the sentence into words
            sentence = sentence.strip().split()
            for word in sentence:
                #add each new word in the sentence to the list of states
                if word not in self.states:
                    self.states.append(word)
                    #helps create corresponding index of where word will be in transition matrix
                    self.wordToindex.update({self.states[len(self.states)-1] : len(self.states) -1})
                    self.indexToWord.update({len(self.states)-1 : self.states[len(self.states)-1]})
            #add 1 to the transition matrix entry corresponding to transitioning from state start to the first word in the sentence
            self.transition[self.wordToindex[sentence[0]],self.wordToindex["$tart"]] += 1
 
            #add 1 to each consecutive pair(x,y) of words in the corresponding entry of the transition matrix
            for i in range(0,len(sentence)-1):
                self.transition[self.wordToindex[sentence[i+1]],self.wordToindex[sentence[i]]] += 1
            #add 1 to the entry of the transition matrix corresponding to the transitioning from the last word in the sentence to stop state
            self.transition[self.wordToindex["$top"],self.wordToindex[sentence[len(sentence)-1]]] += 1
        self.transition[self.wordToindex["$top"],self.wordToindex["$top"]] = 1
        self.transition = self.transition / self.transition.sum(axis=0)
        self.indices = np.arange(0,len(self.indexToWord))
        return
        raise NotImplementedError("Problem 5 Incomplete")

    def babble(self):
        """Begin at the start sate and use the strategy from
        four_state_forecast() to transition through the Markov chain.
        Keep track of the path through the chain and the corresponding words.
        When the stop state is reached, stop transitioning and terminate the
        sentence. Return the resulting sentence as a single string.
        """
        string = ""
        sentence = []
        #initialize going from start to first word 
        sentence.append(self.indexToWord[sum(np.random.multinomial(1,self.transition[:,0]) * self.indices)])
        #run until $top is produced
        while sentence[len(sentence)-1] != "$top":
            sentence.append(self.indexToWord[sum(np.random.multinomial(1,self.transition[:,self.wordToindex[sentence[len(sentence)-1]]]) * self.indices)])
        #since $top isn't part of actual sentence, remove it
        sentence.remove("$top")
        #turn list into string
        for i in range(0,len(sentence)):
            if i == 0:
                string = sentence[i]
            else:
                string = string + " " + sentence[i]
        return string
        raise NotImplementedError("Problem 6 Incomplete")
