$ ->
  $.get "/accounts", (rows) ->
    $.each rows, (index, row) ->
      code = $("<td>").text row.code
      name = $("<td>").text row.name
      type_1 = $("<td>").text row.type_1
      description = $("<td>").text row.description
      link = $("<td>").html '<a href="/productor_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/productor_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/productor_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(code).append(name).append(type_1).append(description).append(link)