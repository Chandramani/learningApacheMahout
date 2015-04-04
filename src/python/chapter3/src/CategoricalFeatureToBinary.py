__author__ = 'ctiwary'

import pandas as pd
import patsy


class CategoricalFeatureToBinary:
    def __init__(self):
        pass

    # read the csv into a data frame
    df = pd.read_csv("../../../../data/chapter3/adult.data.csv")
    # print the column headers
    print df.columns.values
    #convert the selected column to binary columns
    df_converted = patsy.dmatrix('sex - 1', df, return_type='dataframe')
    # print the converted data, first five lines by default
    print df_converted.head()
    # drop the selected column
    df.drop('sex', inplace=True, axis=1)
    # concatenate the two data frames together
    df = pd.concat([df_converted, df], axis=1)
    # print the column headers again
    print df.columns.values
    # write the converted csv data frame to csv
    df.to_csv("../../../../data/chapter3/adult.data.converted_to_binary.csv", index=False)

