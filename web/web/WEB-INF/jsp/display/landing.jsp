<%@ include file="../include.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="description" content=""/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <title>Receiptofi, Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/stylelogin.css"/>

    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.2.5/highcharts.js"></script>
    <script src="//cdnjs.cloudflare.com/ajax/libs/highcharts/4.2.5/modules/exporting.js"></script>

    <script src="${pageContext.request.contextPath}/static/js/classie.js"></script>
    <script>
        function init() {
            window.addEventListener('scroll', function (e) {
                var distanceY = window.pageYOffset || document.documentElement.scrollTop,
                        shrinkOn = 300,
                        header = document.querySelector("header");
                if (distanceY > shrinkOn) {
                    classie.add(header, "smaller");
                } else {
                    if (classie.has(header, "smaller")) {
                        classie.remove(header, "smaller");
                    }
                }
            });
        }
        window.onload = init();

        function documentProcessingPace() {
            var json = $.ajax({
                type: "GET",
                url: '${pageContext. request. contextPath}/display/documentProcessingPace.json',
                async: false
            }).success(function () {
                setTimeout(function () {
                    documentProcessingPace();
                }, 300000);
            }).responseText;
            console.log(json);

            var obj = JSON.parse(json);
            $('#pending').html(obj.pending);
            $('#processedToday').html(obj.processedToday);
        }

        $(function () {
            documentProcessingPace();
        });
    </script>

    <script>
        // Load the fonts
        Highcharts.createElement('link', {
            href: '//fonts.googleapis.com/css?family=Unica+One',
            rel: 'stylesheet',
            type: 'text/css'
        }, null, document.getElementsByTagName('head')[0]);

        Highcharts.theme = {
            colors: ["#2b908f", "#90ee7e", "#f45b5b", "#7798BF", "#aaeeee", "#ff0066", "#eeaaee", "#55BF3B", "#DF5353", "#7798BF", "#aaeeee"],
            chart: {
                backgroundColor: {
                    linearGradient: {x1: 0, y1: 0, x2: 1, y2: 1},
                    stops: [
                        [0, '#2a2a2b'],
                        [1, '#3e3e40']
                    ]
                },
                style: {
                    fontFamily: "'Arial', 'Helvetica', 'Unica One', sans-serif"
                },
                plotBorderColor: '#606063'
            },
            title: {
                style: {
                    color: '#E0E0E3',
                    textTransform: 'uppercase',
                    fontSize: '20px'
                }
            },
            subtitle: {
                style: {
                    color: '#E0E0E3',
                    textTransform: 'uppercase'
                }
            },
            xAxis: {
                gridLineColor: '#707073',
                labels: {
                    style: {
                        color: '#E0E0E3'
                    }
                },
                lineColor: '#707073',
                minorGridLineColor: '#505053',
                tickColor: '#707073',
                title: {
                    style: {
                        color: '#A0A0A3'

                    }
                }
            },
            yAxis: {
                gridLineColor: '#707073',
                labels: {
                    style: {
                        color: '#E0E0E3'
                    }
                },
                lineColor: '#707073',
                minorGridLineColor: '#505053',
                tickColor: '#707073',
                tickWidth: 1,
                title: {
                    style: {
                        color: '#A0A0A3'
                    }
                }
            },
            tooltip: {
                backgroundColor: 'rgba(0, 0, 0, 0.85)',
                style: {
                    color: '#F0F0F0'
                }
            },
            plotOptions: {
                series: {
                    dataLabels: {
                        color: '#B0B0B3'
                    },
                    marker: {
                        lineColor: '#333'
                    }
                },
                boxplot: {
                    fillColor: '#505053'
                },
                candlestick: {
                    lineColor: 'white'
                },
                errorbar: {
                    color: 'white'
                }
            },
            legend: {
                itemStyle: {
                    color: '#E0E0E3'
                },
                itemHoverStyle: {
                    color: '#FFF'
                },
                itemHiddenStyle: {
                    color: '#606063'
                }
            },
            credits: {
                style: {
                    color: '#666'
                }
            },
            labels: {
                style: {
                    color: '#707073'
                }
            },

            drilldown: {
                activeAxisLabelStyle: {
                    color: '#F0F0F3'
                },
                activeDataLabelStyle: {
                    color: '#F0F0F3'
                }
            },

            navigation: {
                buttonOptions: {
                    symbolStroke: '#DDDDDD',
                    theme: {
                        fill: '#505053'
                    }
                }
            },

            // scroll charts
            rangeSelector: {
                buttonTheme: {
                    fill: '#505053',
                    stroke: '#000000',
                    style: {
                        color: '#CCC'
                    },
                    states: {
                        hover: {
                            fill: '#707073',
                            stroke: '#000000',
                            style: {
                                color: 'white'
                            }
                        },
                        select: {
                            fill: '#000003',
                            stroke: '#000000',
                            style: {
                                color: 'white'
                            }
                        }
                    }
                },
                inputBoxBorderColor: '#505053',
                inputStyle: {
                    backgroundColor: '#333',
                    color: 'silver'
                },
                labelStyle: {
                    color: 'silver'
                }
            },

            navigator: {
                handles: {
                    backgroundColor: '#666',
                    borderColor: '#AAA'
                },
                outlineColor: '#CCC',
                maskFill: 'rgba(255,255,255,0.1)',
                series: {
                    color: '#7798BF',
                    lineColor: '#A6C7ED'
                },
                xAxis: {
                    gridLineColor: '#505053'
                }
            },

            scrollbar: {
                barBackgroundColor: '#808083',
                barBorderColor: '#808083',
                buttonArrowColor: '#CCC',
                buttonBackgroundColor: '#606063',
                buttonBorderColor: '#606063',
                rifleColor: '#FFF',
                trackBackgroundColor: '#404043',
                trackBorderColor: '#404043'
            },

            // special colors for some of the
            legendBackgroundColor: 'rgba(0, 0, 0, 0.5)',
            background2: '#505053',
            dataLabelsColor: '#B0B0B3',
            textColor: '#C0C0C0',
            contrastTextColor: '#F0F0F3',
            maskColor: 'rgba(255,255,255,0.3)'
        };

        // Apply the theme
        Highcharts.setOptions(Highcharts.theme);
    </script>
    <script>
        $(function () {
            var chart;
            $(document).ready(function () {
                var options = {
                    chart: {
                        renderTo: 'container'
                    },
                    title: {
                        text: 'Document Processed Daily',
                        x: -20 //center
                    },
                    credits: {
                        enabled: false
                    },
                    subtitle: {
                        text: 'Receiptofi Inc',
                        x: -20
                    },
                    xAxis: {
                        categories: []
                    },
                    yAxis: {
                        title: {
                            text: 'Total Count'
                        },
                        plotLines: [{
                            value: 0,
                            width: 1,
                            color: '#808080'
                        }]
                    },
                    tooltip: {
                        valueSuffix: ''
                    },
                    legend: {
                        layout: 'horizontal',
                        align: 'center',
                        verticalAlign: 'bottom',
                        borderWidth: 0
                    },
                    series: []
                };

                var seriesData = [];
                $.getJSON('${pageContext. request. contextPath}/display/loadStats.json', function (json) {
                    $.map(json, function (value, key) {
                        $.map(value, function (count, day) {
                            if (day !== 'dates') {
                                var series = {
                                    name: '',
                                    data: []
                                };
                                series.name = day;
                                series.data = count;
                                seriesData.push(series)
                            } else {
                                options.xAxis.categories = count;
                            }
                        });
                    });

                    options.series = seriesData;

                    // Create the chart
                    chart = new Highcharts.Chart(options);
                });
            });
        });
    </script>
</head>

<body>
<div class="header_main">
    <div class="header_wrappermain">
        <div class="header_wrapper">
            <div class="header_left_contentmain">
                <div id="logo">
                    <h1><img src="https://www.receiptofi.com/img/Receipt-26x26.png" style="margin: -3px 0;"/><a href="/access/landing.htm">Receiptofi</a></h1>
                </div>
            </div>
            <div class="header_right_login">
                <a class="top-account-bar-text" style="margin-top: -1px;" href="#">
                    <form action="${pageContext.request.contextPath}/access/signoff.htm" method="post">
                        <input type="submit" value="LOG OUT" class="logout_btn"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </a>
                <a class="top-account-bar-text" href="/display/cronStats.htm">STATS</a>
                <a class="top-account-bar-text user-email" href="#">
                    <sec:authentication property="principal.username" />
                </a>
            </div>
        </div>
    </div>
</div>

<header>
</header>
<div class="main clearfix">
    <table width="100%" style="padding: 0px 10px 15px 10px; border: none">
        <tr>
            <td width="20%" style="text-align: center;">
                <table align="right" style="border: none">
                    <tr>
                        <td style="color: yellowgreen; font-size: 100px; font-family: Arial, Helvetica, sans-serif;">
                            <div id="processedToday">${processedToday}</div>
                        </td>
                    </tr>
                    <tr>
                        <td style="font-size: 18px; letter-spacing: 2px; font-family: Arial, Helvetica, sans-serif; font-weight: bold;">
                            Processed Today
                        </td>
                    </tr>
                </table>
            </td>

            <td width="60%" style="text-align: center;">
                <table align="right" style="border: none">
                    <tr>
                        <td style="color: darkred; font-size: 275px; font-family: Arial, Helvetica, sans-serif;">
                            <div id="pending">${pending}</div>
                        </td>
                    </tr>
                    <tr>
                        <td style="font-size: 18px; letter-spacing: 2px; font-family: Arial, Helvetica, sans-serif; font-weight: bold;">
                            Pending
                        </td>
                    </tr>
                </table>
            </td>
            <td width="20%" style="text-align: center;">
                <table align="left" style="border: none">
                    <tr>
                        <td style="color: darkred; font-size: 175px; font-family: Arial, Helvetica, sans-serif;">
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td style="font-size: 10px; letter-spacing: 1px; font-family: Arial, Helvetica, sans-serif; font-weight: bold;">
                            * Count updated every 5 minutes
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <br/>
    <div id="container" style="min-width: 310px; height: 500px; margin: 0 auto; padding: 0px 10px 15px 10px;"></div>
</div>
<div class="footer-tooth clearfix">
    <div class="footer-tooth-left"></div>
    <div class="footer-tooth-middle"></div>
    <div class="footer-tooth-right"></div>
</div>
<div class="big_footer">
    <div class="mfooter_up">
    </div>
    <div class="mfooter_down">
        <p class="footer_copy">&#169; 2016 RECEIPTOFI, INC. ALL RIGHTS RESERVED.
    </div>
</div>
</body>
</html>