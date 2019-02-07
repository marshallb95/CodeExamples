# solutions.py
"""Volume 2: Gaussian Quadrature.
Brandon Marshall
Section 2
1/24/19
"""
import math
import scipy.sparse as sparse
import numpy as np
from scipy.stats import norm
from matplotlib import pyplot as plt
from scipy.integrate import quad
class GaussianQuadrature:
    """Class for integrating functions on arbitrary intervals using Gaussian
    quadrature with the Legendre polynomials or the Chebyshev polynomials.
    """
    # Problems 1 and 3
    def __init__(self, n, polytype="legendre"):
        """Calculate and store the n points and weights corresponding to the
        specified class of orthogonal polynomial (Problem 3). Also store the
        inverse weight function w(x)^{-1} = 1 / w(x).

        Parameters:
            n (int): Number of points and weights to use in the quadrature.
            polytype (string): The class of orthogonal polynomials to use in
                the quadrature. Must be either 'legendre' or 'chebyshev'.

        Raises:
            ValueError: if polytype is not 'legendre' or 'chebyshev'.
        """
        #intialize  
        self.n = n
        #error check
        if polytype != "legendre" and polytype != "chebyshev":
            raise ValueError("polytype must be legendre or chebyshev")
        self.label = polytype
        if polytype == "legendre":
            self.w = lambda x: 1
        else:
            self.w = lambda x: np.sqrt(1-x**2)
        #intialize using problem 2
        self.x, self.weights = self.points_weights(n)
        return
        raise NotImplementedError("Problem 1 Incomplete")

    # Problem 2
    def points_weights(self, n):
        """Calculate the n points and weights for Gaussian quadrature.

        Parameters:
            n (int): The number of desired points and weights.

        Returns:
            points ((n,) ndarray): The sampling points for the quadrature.
            weights ((n,) ndarray): The weights corresponding to the points.
        """
        #determine which polynomial type to use for B,u,a
        if self.label == "legendre":
            B = [((k**2)/(4*k**2 -1)) for k in range(1,n)]
            u = 2
        else:
            B= [1/4 for k in range(2,n)]
            B.insert(0,1/2)
            u = np.pi
        a = [0 for k in range(1,n+1)]

        #build Jacobian matrix
        diagnols = [np.sqrt(B), a, np.sqrt(B)]
        offsets = [-1,0,1]
        J = sparse.diags(diagnols,offsets,(n,n)).toarray()

        #find eigenvalues and eigenvectors of Jacobina
        x, vecs = np.linalg.eig(J)

        #x points are eigenvalues, weights are u*first slot of eigenvector squared
        w = u*((vecs[0,:])**2)
        return x,w 
        raise NotImplementedError("Problem 2 Incomplete")

    # Problem 3
    def basic(self, f):
        """Approximate the integral of a f on the interval [-1,1]."""
        #integral = sum g(xi)*wi where g(x) = f(x)/w(x)
        g = lambda x: f(x) * self.w(x)
        return np.sum(g(self.x)*self.weights)
        raise NotImplementedError("Problem 3 Incomplete")

    # Problem 4
    def integrate(self, f, a, b):
        """Approximate the integral of a function on the interval [a,b].

        Parameters:
            f (function): Callable function to integrate.
            a (float): Lower bound of integration.
            b (float): Upper bound of integration.

        Returns:
            (float): Approximate value of the integral.
        """
        #h(x) = f((b-a)/2 + (a+b)/2)
        #g(x) = h(x) / w(x)
        #returns (b-a)/2 * sum (g(xi)*wi)
        h = lambda x: f(((b-a)/2)*x + ((a+b)/2))
        return ((b-a)/2)*self.basic(h)
        NotImplementedError("Problem 4 Incomplete")

    # Problem 6.
    def integrate2d(self, f, a1, b1, a2, b2):
        """Approximate the integral of the two-dimensional function f on
        the interval [a1,b1]x[a2,b2].

        Parameters:
            f (function): A function to integrate that takes two parameters.
            a1 (float): Lower bound of integration in the x-dimension.
            b1 (float): Upper bound of integration in the x-dimension.
            a2 (float): Lower bound of integration in the y-dimension.
            b2 (float): Upper bound of integration in the y-dimension.

        Returns:
            (float): Approximate value of the integral.
        """
        
        news = np.empty(0)

        #g(x,y) = f(x,y) *w(x)*w(y)
        h = lambda x,y: f(((b1-a1)/2) * x + (a1+b1)/2,((b2-a2)/2) * y + (a2+b2)/2)
        g = lambda x,y: h(x,y) * self.w(x) * self.w(y)

        #build nxn matrix where each row = x points
        z = np.ones((self.n,self.n))
        z = z * self.x

        #build nxn matrix where each row = weights
        Weig = np.ones((self.n,self.n))
        Weig = Weig * self.weights

        #use array broadcasting and fancy indexing to compute g(zi,zj)*wi*wj
        for i in range(0,self.n):
             #news builds each index as the inner sum of sum(i = 1 to n) sum (j = 1 to n) of g(zi,zj)*wi*wj
             news = np.append(news, np.sum(g(z[:,i],z[i,:]) * Weig[:,i]*Weig[i,:]))
        #returns outer sum * (b1-a1)*(b2-a2)/4
        return ((b1-a1)*(b2-a2)/4)*np.sum(news)
        raise NotImplementedError("Problem 6 Incomplete")


# Problem 5
def prob5():
    """Use scipy.stats to calculate the "exact" value F of the integral of
    f(x) = (1/sqrt(2 pi))e^((-x^2)/2) from -3 to 2. Then repeat the following
    experiment for n = 5, 10, 15, ..., 50.
        1. Use the GaussianQuadrature class with the Legendre polynomials to
           approximate F using n points and weights. Calculate and record the
           error of the approximation.
        2. Use the GaussianQuadrature class with the Chebyshev polynomials to
           approximate F using n points and weights. Calculate and record the
           error of the approximation.
    Plot the errors against the number of points and weights n, using a log
    scale for the y-axis. Finally, plot a horizontal line showing the error of
    scipy.integrate.quad() (which doesn’t depend on n).
    """
    #initialize
    ErrLeg = []
    ErrCheb = []
    ErrScipy = []

    #calculate exact value of the integral 
    F = norm.cdf(2) - norm.cdf(-3)

    #build function
    f = lambda x: ((1/np.sqrt(2*np.pi))*np.exp((-1*x**2)/2))
    n_list = [n for n in range(5,55,5)]

    #for each n, find n+1 Gaussian Quadrature points and weights, estimate the integral using chebyshev and legendre polynomails
    for n in n_list:
        Appr = GaussianQuadrature(n)
        approx = Appr.integrate(f,-3,2)
        ErrLeg.append(abs(F - approx))
        Appr = GaussianQuadrature(n,"chebyshev")
        approx = Appr.integrate(f,-3,2)
        ErrCheb.append(abs(F-approx))
        approx= quad(f,-3,2)[0]
        ErrScipy.append(abs(F-approx))
    #find the absolute difference of the approximations and plot each versus number of points
    plt.plot(n_list,ErrLeg,'r',label = "Legendre Error")
    plt.plot(n_list,ErrCheb, 'b', label = "Chebyshev Error")
    plt.plot(n_list,ErrScipy, 'g', label = "Scipy Error")
    plt.yscale("log")
    plt.xlabel("# of points and weigts")
    plt.ylabel("size of error")
    plt.title("Error of Approximations")
    plt.legend(loc = "upper left")
    plt.show()
    return
    raise NotImplementedError("Problem 5 Incomplete")
