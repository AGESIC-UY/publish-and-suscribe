# Controlamos que estemos corriendo con root
if [ "$(whoami)" != "root" ]; then
  echo "Por favor ejecute con SUDO/ROOT"
  exit
fi

# Vamos a la carpeta de la PDI
cd /opt/pdi

# Copiamos los archivos de servicio a la carpeta de systemd
cp *.service /etc/systemd/system

# Habilitamos todos los servicios en systemd
for SRV in `ls *.service`; do
    systemctl enable $SRV
done


