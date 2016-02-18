$ ->
  $.get "/proveedores", (proveedores) ->
    $.each proveedores, (index, proveedor) ->
      nombre = $("<td>").text proveedor.nombre
      cuenta = $("<td>").text proveedor.cuenta
      $("#proveedores").append $("<tr>").append(nombre).append(cuenta)
