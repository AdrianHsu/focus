var svg_height = 400;
var svg_width = 1000;
var bar_padding = 5;

var dataset = [30, 20, 10, 40, 45, 25, 15, 35, 18];

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
        return svg_height - (d * 4);
    })
    .attr("width", svg_width / dataset.length - bar_padding)
    .attr("height", function(d) {
        return d * 4;
    })
    .attr("fill", function(d) {
        return "rgba(0, 100, " + d * 5 + ", 0.95)";
    });
 //    .on("click", function(d) {
 //          rectClick(this, d);
	// })                  
	// .on("mouseout", function(d) {
 //          rectMouseOut(this, d);
	// })

// function rectClick(_svg, d) {
//     d3.select(_svg).transition().duration(500)
//         .attr("y", function(d) {
//             return 0;
//         })
//         .attr("height", function(d) {
//             return svg_height - 0;
//         })
//         .attr("fill", "pink");
//         // .attr("fill", color);
// };

// function rectMouseOut(_svg, d) {
//     d3.select(_svg).transition().duration(500)
//         .attr("y", function(d) {
//             return svg_height - (d * 4);
//         })
//         .attr("height", function(d) {
//             return d * 4;
//         })
//         .attr("fill", "rgba(0, 100, " + d * 5 + ", 0.75)");
// };
svg.selectAll("text")
	.data(dataset)
	.enter()
	.append("text")
	.text(function(d) {
		return d;
	})
	.attr("text-anchor", "right")
	.attr("x", function(d, i) {
		return i * (svg_width / dataset.length) + 
			((svg_width / dataset.length - bar_padding) / 2);
	})
	.attr("y", function(d) {
		return svg_height - (d * 4) + 20;
	})
	.attr("fill", "#FFFFFF")
	.attr("font-family", "sans-serif")
	.attr("font-size", "12px");
	.attr("fill", "#FFFFFF");
