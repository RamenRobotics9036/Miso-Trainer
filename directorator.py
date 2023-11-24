import os
import argparse
import shutil

# Function to create the output directory and handle naming collisions
def create_output_directory(output_dir):
    if os.path.exists(output_dir):
        raise FileExistsError(f"Output directory '{output_dir}' already exists. Please choose a different name or delete the existing directory.")
    os.makedirs(output_dir)

# Function to copy Java files to the output directory
def copy_java_files(directory, output_dir):
    for root, _, files in os.walk(directory):
        for java_file in files:
            if java_file.endswith(".java"):
                source_path = os.path.join(root, java_file)
                base_name = os.path.basename(source_path)
                output_path = os.path.join(output_dir, base_name)
                
                # Handle naming collisions by appending (1), (2), etc.
                count = 1
                while os.path.exists(output_path):
                    name, extension = os.path.splitext(base_name)
                    new_name = f"{name} ({count}){extension}"
                    output_path = os.path.join(output_dir, new_name)
                    count += 1
                
                shutil.copy2(source_path, output_path)
                print(f"Copied: {source_path} to {output_path}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Copy Java files to an output directory")
    parser.add_argument("directory", help="Directory containing Java files")
    parser.add_argument("output_dir", help="Output directory to copy Java files")

    args = parser.parse_args()

    directory_to_search = args.directory
    output_dir = args.output_dir

    if not os.path.exists(directory_to_search):
        print(f"The specified directory does not exist: {directory_to_search}")
    else:
        create_output_directory(output_dir)
        copy_java_files(directory_to_search, output_dir)
        print(f"Copied Java files from {directory_to_search} to {output_dir}")
