/*
Author: Saagar Kamrani
Date: 2018-07-01
Description: Javascript used to clean year data and convert to appropriate year format
18 becomes 2018
99 becomes 1999
2002 remains 2002
*/

var output = MakeBuildYear(input);

function MakeBuildYear(input) {
    if (input !== null) {

        if (input.length == 1 && isInt(input)) {

            var out = '200' + Math.abs(input);

        } else if (input.length == 2 && isInt(input)) {

            if (input > 50) {
                var out = '19' + Math.abs(input);
            } else {
                var out = '20' + input;
            }

        } else if (input.length == 4 && isInt(input)) {
            var out = Math.abs(input);
        } else {
            var out = '';
        }

    } else {
        var out = null;
    }

    return out;
}

function isInt(value) {
    return !isNaN(value) && (function(x) {
        return (x | 0) === x;
    })(parseFloat(value))
}
