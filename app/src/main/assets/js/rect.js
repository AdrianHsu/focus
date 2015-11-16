var dataset;
var color = ["#CF5C60", "#F3AE4E", "#4AB471", "#4EB1CB"];
var name = ["Youtube", "Snapchat", "Line", "Facebook"]
loadRectChart([30, 35, 25, 10]);

function createRect() {
    var svg_height = screen.height * 0.75;
    var svg_width = screen.width * 0.95;
    var a = svg_height / 60;
    var bar_padding = 1;

    var svg = d3.select("body")
        .append("svg")
        .attr("height", svg_height)
        .attr("width", svg_width);

    svg.selectAll("rect")
        .data(dataset)
        .enter()
        .append("rect")
        .attr("x", function(d, i) {
            return i * (svg_width / dataset.length);
        })
        .attr("y", function(d) {
            return svg_height;
            // return svg_height;
        })
        .attr("width", svg_width / dataset.length - bar_padding)
        .attr("height", function(d) {
            // return d * a;
            return 0;
        })
        .attr("fill", function(d, i) {
            return color[i];
        })
        .on("click", function(d, i) {
            
            var pink = i;
            d3.selectAll("rect").transition().duration(500)
            .attr("fill", function(d, i) {
                if(i != pink) {
                    return color[i];
                } else {
                    return "pink";
                }
            })
        })
        .transition().delay(500).duration(1000).attr("height", function(d) {
            return d * a;
            // return svg_height;
        }).attr("y", function(d) {
            return svg_height - d * a;
        });             

    svg.selectAll("text")
        .data(dataset)
        .enter()
        .append("text")
        .text( function(d) {
            return 0 + "%";
        })
        .attr("text-anchor", "middle")
        .attr("x", function(d, i) {
            return i * (svg_width / dataset.length) +
                ((svg_width / dataset.length - bar_padding) / 2);
        })
        .attr("y", function(d) {
            return svg_height - (d * a);
        })
        .attr("fill", "rgba(255, 255, 255, 1)")
        .attr("font-size", "30")
        .transition().delay(500).duration(1000).attr("y", function(d) {
            return svg_height - (d * a) - 30;
        })
        .tween('number', function(d) {
            var i = d3.interpolateRound(0, d);
            return function(t) {
                this.textContent = i(t) + "%";
            };
        });

    svg.append("text")
        .attr("x", (svg_width / 2))
        .attr("y", 16)
        .attr("text-anchor", "middle")
        .style("font-size", "16px")
        .style("text-decoration", "underline")
        .text("Bar Chart, FOCUS")
        .attr("fill", "#FFFFFF");


}

function loadRectChart(array) {
    dataset = array;
    createRect();
}
function rectClick(_svg, d, i) {
    d3.select(_svg).transition().duration(500)
        .attr("fill", "pink");

};

// function rectMouseOut(_svg, d, i) {
//     d3.select(_svg).transition().duration(500)
//         .attr("fill", color[i]);
// };
