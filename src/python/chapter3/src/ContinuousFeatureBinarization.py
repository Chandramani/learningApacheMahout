__author__ = 'ctiwary'

import pandas as pd
from sklearn import preprocessing


class ContinuousFeatureBinning:
    def __init__(self):
        pass

    df = pd.read_csv("../../../../data/chapter3/adult.data.csv")
    print df['age'].head()
    binarizer = preprocessing.Binarizer(threshold=40)
    print binarizer.transform(df['age'])[0:5]