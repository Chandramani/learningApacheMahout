__author__ = 'ctiwary'

import pandas as pd


class ContinuousFeatureBinning:
    def __init__(self):
        pass

    df = pd.read_csv("../../../../data/chapter3/adult.data.csv")
    print df['age'].describe()
    df['age'] = pd.qcut(x=df['age'],q=4,labels=['Young','Adult','MiddleAge','Old'])
    print df['age'].unique()


