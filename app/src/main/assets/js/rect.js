var svg_height = screen.height * 0.75;
var svg_width = screen.width * 0.95;
var a = svg_height / 60;
var bar_padding = 1;

var dataset = [30, 35, 25, 10];

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
        return svg_height - (d * a);
    })
    .attr("width", svg_width / dataset.length - bar_padding)
    .attr("height", function(d) {
        return d * a;
    })
    .attr("fill", function(d) {
        return "rgba(0, 100, " + d * 5 + ", 0.75)";
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
// svg.selectAll("text")
// 	.data(dataset)
// 	.enter()
// 	.append("text")
// 	.text(function(d) {
// 		return d + "%";
// 	})
// 	.attr("text-anchor", "right")
// 	.attr("x", function(d, i) {
// 		return i * (svg_width / dataset.length) + 
// 			((svg_width / dataset.length - bar_padding) / 2);
// 	})
// 	.attr("y", function(d) {
// 		return svg_height - (d * 4) + 20;
// 	})
// 	.attr("fill", "#FFFFFF")
// 	.attr("font-size", "14px");
// 	.attr("fill", "#FFFFFF");
