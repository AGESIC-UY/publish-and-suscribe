# Controlamos que estemos corriendo con root
if [ "$(whoami)" != "root" ]; then
  echo "Por favor ejecute con SUDO/ROOT"
  exit
fi

# Ejecutamos en la carpeta assembler

# Creamos la carpeta donde van los microservicios
if [ ! -d /opt/pdi ]; then
    mkdir /opt/pdi
fi

# Ponemos lo nuevo
cp ./deploy/* /opt/pdi

# Ajustamos los permisos para dejar ejecutables los comandos
chmod ugo+x /opt/pdi/*.sh
