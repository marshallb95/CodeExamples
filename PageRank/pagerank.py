# solutions.py
"""Volume 1: The Page Rank Algorithm.
Brandon Marshall
Section 2
3/5/19
"""

import numpy as np
import networkx as nx
from itertools import combinations
# Problems 1-2
class DiGraph:
    """A class for representing directed graphs via their adjacency matrices.

    Attributes:
        (fill this out after completing DiGraph.__init__().)
    """
    # Problem 1
    def __init__(self, A, labels=None):
        """Modify A so that there are no sinks in the corresponding graph,
        then calculate Ahat. Save Ahat and the labels as attributes.

        Parameters:
            A ((n,n) ndarray): the adjacency matrix of a directed graph.
                A[i,j] is the weight of the edge from node j to node i.
            labels (list(str)): labels for the n nodes in the graph.
                If None, defaults to [0, 1, ..., n-1].
        """
        #initialize
        self.n = len(A)
        #If a column is zero, make it all ones
        for i in range(self.n):
             if np.allclose(A[:,i],np.zeros(self.n)) == True:
                 A[:,i] = np.ones(self.n)
        #build Ahat
        self.Ahat = A / np.sum(A,axis=0)
        #If there are no labels, assign labels 0-n
        if labels == None:
            self.labels = list(range(self.n))
        #if labels aren't same length is A
        elif len(labels) != self.n:
            raise ValueError("Number of labels does not equal number of nodes in the graph")
        else:
            self.labels = labels
        return
        raise NotImplementedError("Problem 1 Incomplete")

    # Problem 2
    def linsolve(self, epsilon=0.85):
        """Compute the PageRank vector using the linear system method.

        Parameters:
            epsilon (float): the damping factor, between 0 and 1.

        Returns:
            dict(str -> float): A dictionary mapping labels to PageRank values.
        """
        #initialize
        I = np.identity(self.n)
        ONE = np.ones(self.n)
        #solve (I-epsilon*Ahat)p = (1-epsilon)/n * 1(vector)
        values = np.linalg.solve(I-epsilon*self.Ahat,((1-epsilon)/self.n)*np.ones(self.n))
        return dict(zip(self.labels,values))
        raise NotImplementedError("Problem 2 Incomplete")

    # Problem 2
    def eigensolve(self, epsilon=0.85):
        """Compute the PageRank vector using the eigenvalue method.
        Normalize the resulting eigenvector so its entries sum to 1.

        Parameters:
            epsilon (float): the damping factor, between 0 and 1.

        Return:
            dict(str -> float): A dictionary mapping labels to PageRank values.
        """
        #B = epsilon*Ahat + (1-epsilon)/n * E)
        B = epsilon*self.Ahat + ((1-epsilon)/self.n)*np.ones((self.n,self.n))
        #find eigenvalues of p
        eigvals = np.linalg.eig(B)
        #get largest eigenvector
        P = eigvals[1][:,0]
        #normalize
        values = P / np.linalg.norm(P,ord=1)
        return dict(zip(self.labels,values))
        raise NotImplementedError("Problem 2 Incomplete")

    # Problem 2
    def itersolve(self, epsilon=0.85, maxiter=100, tol=1e-12):
        """Compute the PageRank vector using the iterative method.

        Parameters:
            epsilon (float): the damping factor, between 0 and 1.
            maxiter (int): the maximum number of iterations to compute.
            tol (float): the convergence tolerance.

        Return:
            dict(str -> float): A dictionary mapping labels to PageRank values.
        """
        #initialize
        p0 = np.ones(self.n)/ self.n
        p1 = epsilon*(self.Ahat @ p0) + ((1-epsilon)/self.n)
        count = 1
        #run till either converges or max iterates
        while np.linalg.norm(p1-p0) >= tol and count <= maxiter:
            count += 1
            p0 = p1
            p1 = epsilon*(self.Ahat @ p0) + ((1-epsilon)/self.n)
        return dict(zip(self.labels,p1))
        raise NotImplementedError("Problem 2 Incomplete")


# Problem 3
def get_ranks(d):
    """Construct a sorted list of labels based on the PageRank vector.

    Parameters:
        d (dict(str -> float)): a dictionary mapping labels to PageRank values.

    Returns:
        (list) the keys of d, sorted by PageRank value from greatest to least.
    """
    #initialize
    val = list(d.values())
    keys = list(d.keys())
    sortKeys = []
    #sort values from max to min
    sort = np.argsort(val)[::-1]
    #add corresponding keys in same order(returns keys from max to min)
    for i in sort:
        sortKeys.append(keys[i])
    return sortKeys
    raise NotImplementedError("Problem 3 Incomplete")


# Problem 4
def rank_websites(filename="web_stanford.txt", epsilon=0.85):
    """Read the specified file and construct a graph where node j points to
    node i if webpage j has a hyperlink to webpage i. Use the DiGraph class
    and its itersolve() method to compute the PageRank values of the webpages,
    then rank them with get_ranks().

    Each line of the file has the format
        a/b/c/d/e/f...
    meaning the webpage with ID 'a' has hyperlinks to the webpages with IDs
    'b', 'c', 'd', and so on.

    Parameters:
        filename (str): the file to read from.
        epsilon (float): the damping factor, between 0 and 1.

    Returns:
        (list(str)): The ranked list of webpage IDs.
    """
    #initialize
    labels = set()
    dic = dict()
    #get file
    with open(filename,'r') as infile:
        contents = infile.readlines()

    #turn into list of lists, each with website ids
    for i in range(len(contents)):
        contents[i] = contents[i].strip('\n').split('/')
        #build set of unique ids
        for j in contents[i]:
            labels.add(j)
    #number of ids
    n=len(labels)
    #build matrix
    A = np.zeros((n,n))
    #sort ids from least to greatest for indexes
    labels = sorted(labels)
    #build dictionary that assigns id to index
    for i,val in enumerate(labels):
        dic[val] = i
    #for each list in the list of lists
    for website in contents:
        #if link exists, turn corresponding spot in matrix to 1
        for j in range(1,len(website)):
            x = dic[website[0]]
            y = dic[website[j]]
            A[x,y] = 1
    #did it backwards, so A needs to be transposed
    G = DiGraph(A.T,list(labels))
    return get_ranks(G.itersolve(epsilon=epsilon))
    raise NotImplementedError("Problem 4 Incomplete")


# Problem 5
def rank_ncaa_teams(filename, epsilon=0.85):
    """Read the specified file and construct a graph where node j points to
    node i with weight w if team j was defeated by team i in w games. Use the
    DiGraph class and its itersolve() method to compute the PageRank values of
    the teams, then rank them with get_ranks().

    Each line of the file has the format
        A,B
    meaning team A defeated team B.

    Parameters:
        filename (str): the name of the data file to read.
        epsilon (float): the damping factor, between 0 and 1.

    Returns:
        (list(str)): The ranked list of team names.
    """
    #initialize
    labels = set()
    dic = dict()
    #get file
    with open(filename,'r') as infile:
        team = infile.readlines()
    #remove first line
    team.remove("Winner,Loser\n")
    #turn list with winning string and losing string
    for i in range(0,len(team)):
        team[i] = team[i].strip('\n').split(',')
        #build set of unique number of teams
        for j in team[i]:
            labels.add(j)
    #sort teams alphabetically
    labels = sorted(labels)
    #get number of teams
    n = len(labels)
    #initialize matrix
    A = np.zeros((n,n))
    #assign index to each team
    for i,val in enumerate(labels):
        dic[val] = i
    #for each team, add weight from losing to team winning team
    for teams in team:
        winning = dic[teams[0]]
        loosing = dic[teams[1]]
        A[winning][loosing] += 1
    #build and solve
    G = DiGraph(A,list(labels))
    return get_ranks(G.itersolve(epsilon=epsilon))
    raise NotImplementedError("Problem 5 Incomplete")

# Problem 6
def rank_actors(filename="top250movies.txt", epsilon=0.85):
    """Read the specified file and construct a graph where node a points to
    node b with weight w if actor a and actor b were in w movies together but
    actor b was listed first. Use NetworkX to compute the PageRank values of
    the actors, then rank them with get_ranks().

    Each line of the file has the format
        title/actor1/actor2/actor3/...
    meaning actor2 and actor3 should each have an edge pointing to actor1,
    and actor3 should have an edge pointing to actor2.
    """
    #initialize
    temp = set()
    DG = nx.DiGraph()
    #get file
    with open(filename, 'r', encoding="utf-8") as infile:
        movies = infile.readlines()
    #remove / and endlines, as well as movie title
    for i in range(len(movies)):    
        movies[i]  = movies[i].strip('\n').split('/')
        movies[i].pop(0)
        #get number of unique actors
        for actor in movies[i]:
            temp.add(actor)
    #sort alphabetically
    temp = sorted(temp)
    #add each actor as node
    for actor in temp:
        DG.add_node(actor)
    #for each actor in the list, add weight from all actors listed after
    for actors in movies:
        l = list(combinations(actors,2))
        #for each pair in combination, if edge exists, increment weight by one, else increment
        for k in l:
            if DG.has_edge(k[1],k[0]) == False:
                DG.add_edge(k[1],k[0],weight=1)
            else:
               DG[k[1]][k[0]]["weight"] += 1
    #solve pagerank problem
    dic = nx.pagerank(DG,alpha=epsilon)
    return get_ranks(dic)
    raise #NotImplementedError("Problem 6 Incomplete")
