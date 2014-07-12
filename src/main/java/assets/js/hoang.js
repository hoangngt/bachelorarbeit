/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

$(function() {
    $("#update").submit(function() {
        $("#update").hide();
        $("#updateRes").html("Bitte haben Sie etwas Geduld. Ontologie wird gerade aktualisiert. Es kann mehrere Minuten dauern... ");
        $.ajax("/ba_hoang/updateOnto", {
            type: 'POST',
            dataType: 'json',
            contentType : 'application/json',
            success: function (data) {
                $("#updateRes").html(data);
                console.log(data);
            }
        });
        return false;
    });
    
    $('#search').submit(function() {
        $('#modulen').text("Searching..." );

        $.ajax ("/ba_hoang/contactForm", {
            data: JSON.stringify($('form').serializeObject()),
            contentType : 'application/json',
            type : 'POST',
            dataType: 'json',
            success: function(data) {
                console.log(data);
                var item_template = 
                '<div class="item container">' +
                  '<a href="https://studip.uni-halle.de/<%= obj.link %>"><h3><%= obj.modName %></h3></a>' +
                  '<p class="desc"><h4>Dozent</h4><%= obj.dozent %></p>' +
                '</div>';
                var settings = {
                 state           : { filters: {} },
                 items           : data,
                 facets          : { 
                                     'verpSG'     : 'verpflichtet für',
                                     'wahlSG'    : 'wahlbar für',
                                     'dozent'     : 'Dozent'
                 },  
                 resultSelector  : '#modulen',
                 facetSelector   : '#selection',
                 resultTemplate  : item_template,
                 orderByOptions  : {'modName': 'Module name', 'dozent': 'Dozent name', 'verpSG': 'Verpflicht', 'RANDOM': 'Random'}
                };
                $.facetelize(settings);
            },
            error: function() {
                alert("OOps. Something wrong happened!");
            }
 
        });
        return false;
    });
});