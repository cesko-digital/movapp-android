require 'fileutils'

ORIGIN_FOLDER="tmp/data"
ASSETS_FOLDER="app/src/main/assets"

def replace_file(origin, assets)
  if File.file?(origin)
    filename = File.basename(origin) 
    destination = "#{assets}/#{filename}"

    FileUtils.rm_rf(destination)
    FileUtils.cp(origin, destination)
    puts "#{destination} updated ✅"
  end
end

def replace_dir(origin, assets)
  if File.directory?(origin)
    dirname = File.basename(origin) 
    destination = "#{assets}/#{dirname}"

    FileUtils.rm_rf(destination)
    FileUtils.copy_entry(origin, destination)
    puts "#{destination} updated ✅"
  end
end


def main
    system("git clone https://github.com/cesko-digital/movapp-data.git tmp")

    puts "data..."

    Dir.glob("#{ORIGIN_FOLDER}/*-dictionary.json") do |filename|
      next if filename == '.' or filename == '..'
      replace_file(filename, ASSETS_FOLDER)   
    end

    Dir.glob("#{ORIGIN_FOLDER}/*-sounds") do |filename|
      next if filename == '.' or filename == '..'
      replace_dir(filename, ASSETS_FOLDER)
    end

    Dir.glob("#{ORIGIN_FOLDER}/*-alphabet.json") do |filename|
      next if filename == '.' or filename == '..'
      replace_file(filename, ASSETS_FOLDER)   
    end

    Dir.glob("#{ORIGIN_FOLDER}/*-alphabet") do |filename|
      next if filename == '.' or filename == '..'
      replace_dir(filename, ASSETS_FOLDER)
    end

    replace_file("#{ORIGIN_FOLDER}/team.v1.json", ASSETS_FOLDER)

    puts "data ✅"

    puts "images.."

    Dir.glob("#{ORIGIN_FOLDER}/images/android") do |filename|
      next if filename == '.' or filename == '..'
      replace_dir(filename, "#{ASSETS_FOLDER}/images")
    end

    puts "images ✅"

    puts "🧹🧹🧹🧹🧹"
    FileUtils.rm_rf("tmp/", :verbose => true)
    puts "All ✅"
end

main()
