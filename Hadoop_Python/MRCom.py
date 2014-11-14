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

    def comMap(self, _, line):

        filename = jobconf_from_env('map.input.file')
        yield 1, filename
        
    def comRed(self, key, values):

        for val in values:
            yield key, val


if __name__ == '__main__':
    MRCom.run()

