from itertools import combinations
from math import sqrt

from mrjob.job import MRJob
from mrjob.compat import jobconf_from_env

class MRCom(MRJob):

    
    def configure_options(self):
        pass
        super(MRCom, self).configure_options()
        
    def steps(self):
        
        return [
            self.mr(mapper=self.comMap,
                    reducer=self.comRed)
        ]

    def comMap(self, splitNum, split):

        yield splitNum, split
        
    def comRed(self, key, values):

        for val in values:
            yield key, val


if __name__ == '__main__':
    MRJob.HADOOP_INPUT_FORMAT = "BinaryInputFormat"
    MRCom.run()

