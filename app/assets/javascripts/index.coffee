$ ->
  $.get "/persons", (persons) ->
    $.each persons, (index, person) ->
      name = $("<div>").addClass("name").text person.name
      last_name = $("<div>").addClass("last_name").text person.last_name
      age = $("<div>").addClass("age").text person.age
      $("#persons").append $("<li>").append(name).append(last_name).append(age)