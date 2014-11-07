#!/usr/bin/env python

# Returns the square of a value.
def square_it(num):
    return num * num

# Returns the sum of two values.
def sum_two(a, b):
    #print 'Adding %d to %d' % (a, b)
    return a + b

input_list = [1, 2, 3, 4, 5]


# Imperative style:
sum = 0
for num in input_list:
    sum += square_it(num)

# Functional style:
map_output = map(square_it, input_list)
reduce_output = reduce(sum_two, map_output)


print 'Input list:', input_list
print 'Map output:', map_output
print 'Reduce output:', reduce_output

